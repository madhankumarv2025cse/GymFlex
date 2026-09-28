let membershipsList = [];
let membersList = [];
let plansList = [];

let createMembershipModal = null;
let renewMembershipModal = null;

document.addEventListener("DOMContentLoaded", () => {
    checkAuth();
    createMembershipModal = new bootstrap.Modal(document.getElementById("createMembershipModal"));
    renewMembershipModal = new bootstrap.Modal(document.getElementById("renewMembershipModal"));

    loadMemberships();
    loadMembersAndPlansOptions();

    document.getElementById("create-membership-form").addEventListener("submit", handleCreateMembershipSubmit);
    document.getElementById("renew-membership-form").addEventListener("submit", handleRenewMembershipSubmit);

    // Check query params for auto-open renew
    const urlParams = new URLSearchParams(window.location.search);
    const renewId = urlParams.get("renew");
    if (renewId) {
        setTimeout(() => openRenewModal(parseInt(renewId)), 500);
    }
});

async function loadMemberships() {
    const tableBody = document.getElementById("memberships-table-body");
    tableBody.innerHTML = `<tr><td colspan="7" class="text-center py-4"><div class="spinner-border text-primary"></div><p class="mt-2 text-muted mb-0">Loading memberships...</p></td></tr>`;

    try {
        const data = await apiGet("/memberships");
        membershipsList = data || [];
        renderMemberships(membershipsList);
    } catch (error) {
        tableBody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-danger"><i class="bi bi-exclamation-triangle fs-3 d-block mb-2"></i>Unable to load memberships.</td></tr>`;
        showToast(error.message, "danger");
    }
}

async function loadMembersAndPlansOptions() {
    try {
        membersList = await apiGet("/members") || [];
        plansList = await apiGet("/plans") || [];

        // Populate Member Dropdown
        const memberSelect = document.getElementById("memberSelect");
        memberSelect.innerHTML = `<option value="">-- Select Member --</option>`;
        membersList.forEach(m => {
            memberSelect.innerHTML += `<option value="${m.id}">${escapeHtml(m.name)} (${escapeHtml(m.email)})</option>`;
        });

        // Populate Plan Dropdown for Create
        const planSelect = document.getElementById("planSelect");
        planSelect.innerHTML = `<option value="">-- Select Plan --</option>`;
        plansList.forEach(p => {
            planSelect.innerHTML += `<option value="${p.id}">${escapeHtml(p.name)} - ₹${p.price} (${p.durationInMonths} Mo)</option>`;
        });

        // Populate Plan Dropdown for Renew
        const renewPlanSelect = document.getElementById("renewPlanSelect");
        renewPlanSelect.innerHTML = `<option value="">-- Keep Current Plan or Select New --</option>`;
        plansList.forEach(p => {
            renewPlanSelect.innerHTML += `<option value="${p.id}">${escapeHtml(p.name)} - ₹${p.price} (${p.durationInMonths} Mo)</option>`;
        });
    } catch (error) {
        console.error("Error loading dropdown options:", error);
    }
}

function renderMemberships(list) {
    const tableBody = document.getElementById("memberships-table-body");
    if (!list || list.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-muted"><i class="bi bi-inbox fs-3 d-block mb-2"></i>No memberships found.</td></tr>`;
        return;
    }

    let rowsHtml = "";
    list.forEach(m => {
        const isExpired = m.status === "EXPIRED" || new Date(m.endDate) < new Date().setHours(0,0,0,0);
        const badgeClass = isExpired ? "badge-expired" : "badge-active";
        const statusText = isExpired ? "EXPIRED" : "ACTIVE";

        rowsHtml += `
            <tr>
                <td>#${m.id}</td>
                <td class="fw-semibold">${m.member ? escapeHtml(m.member.name) : 'N/A'}</td>
                <td><span class="badge bg-light text-dark border">${m.plan ? escapeHtml(m.plan.name) : 'N/A'}</span></td>
                <td>${m.startDate || 'N/A'}</td>
                <td class="${isExpired ? 'text-danger fw-bold' : ''}">${m.endDate || 'N/A'}</td>
                <td><span class="badge ${badgeClass}">${statusText}</span></td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="openRenewModal(${m.id})">
                        <i class="bi bi-arrow-repeat me-1"></i>Renew
                    </button>
                </td>
            </tr>
        `;
    });
    tableBody.innerHTML = rowsHtml;
}

function filterMemberships(status) {
    document.querySelectorAll(".filter-btn").forEach(b => b.classList.remove("active"));
    event.target.classList.add("active");

    if (status === "ALL") {
        renderMemberships(membershipsList);
    } else if (status === "ACTIVE") {
        renderMemberships(membershipsList.filter(m => m.status === "ACTIVE"));
    } else if (status === "EXPIRED") {
        renderMemberships(membershipsList.filter(m => m.status === "EXPIRED"));
    }
}

function openCreateMembershipModal() {
    document.getElementById("create-membership-form").reset();
    document.getElementById("startDate").value = new Date().toISOString().split('T')[0];
    createMembershipModal.show();
}

async function handleCreateMembershipSubmit(e) {
    e.preventDefault();

    const memberId = parseInt(document.getElementById("memberSelect").value);
    const planId = parseInt(document.getElementById("planSelect").value);
    const startDate = document.getElementById("startDate").value;

    if (isNaN(memberId) || isNaN(planId) || !startDate) {
        showToast("Please select a member, plan, and start date.", "warning");
        return;
    }

    const submitBtn = document.getElementById("create-submit-btn");
    submitBtn.disabled = true;

    try {
        await apiPost("/memberships", { memberId, planId, startDate });
        showToast("Membership created successfully.", "success");
        createMembershipModal.hide();
        loadMemberships();
    } catch (error) {
        showToast(error.message, "danger");
    } finally {
        submitBtn.disabled = false;
    }
}

function openRenewModal(id) {
    const membership = membershipsList.find(m => m.id === id);
    if (!membership) return;

    document.getElementById("renew-membership-id").value = membership.id;
    document.getElementById("renew-member-name").value = membership.member ? membership.member.name : "N/A";
    document.getElementById("renew-current-plan").value = membership.plan ? membership.plan.name : "N/A";
    document.getElementById("renew-current-end").value = membership.endDate;

    renewMembershipModal.show();
}

async function handleRenewMembershipSubmit(e) {
    e.preventDefault();

    const membershipId = document.getElementById("renew-membership-id").value;
    const selectedPlanId = document.getElementById("renewPlanSelect").value;

    const renewData = selectedPlanId ? { planId: parseInt(selectedPlanId) } : {};
    const submitBtn = document.getElementById("renew-submit-btn");
    submitBtn.disabled = true;

    try {
        await apiPost(`/memberships/${membershipId}/renew`, renewData);
        showToast("Membership renewed successfully.", "success");
        renewMembershipModal.hide();
        loadMemberships();
    } catch (error) {
        showToast(error.message, "danger");
    } finally {
        submitBtn.disabled = false;
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}
