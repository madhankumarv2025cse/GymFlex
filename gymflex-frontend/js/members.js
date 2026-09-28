let membersList = [];
let deleteMemberId = null;
let memberModal = null;
let deleteModal = null;

document.addEventListener("DOMContentLoaded", () => {
    checkAuth();
    memberModal = new bootstrap.Modal(document.getElementById("memberModal"));
    deleteModal = new bootstrap.Modal(document.getElementById("deleteModal"));

    loadMembers();

    document.getElementById("member-form").addEventListener("submit", handleMemberFormSubmit);
    document.getElementById("search-input").addEventListener("input", filterMembers);
    document.getElementById("confirm-delete-btn").addEventListener("click", executeDeleteMember);
});

async function loadMembers() {
    const tableBody = document.getElementById("members-table-body");
    tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-4"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div><p class="mt-2 text-muted mb-0">Loading members...</p></td></tr>`;

    try {
        const members = await apiGet("/members");
        membersList = members || [];
        renderMembers(membersList);
    } catch (error) {
        tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-danger"><i class="bi bi-exclamation-triangle fs-3 d-block mb-2"></i>Unable to load members. Please make sure backend server is running.</td></tr>`;
        showToast(error.message, "danger");
    }
}

function renderMembers(list) {
    const tableBody = document.getElementById("members-table-body");
    if (!list || list.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted"><i class="bi bi-inbox fs-3 d-block mb-2"></i>No members found.</td></tr>`;
        return;
    }

    let rowsHtml = "";
    list.forEach(m => {
        rowsHtml += `
            <tr>
                <td>#${m.id}</td>
                <td class="fw-semibold">${escapeHtml(m.name)}</td>
                <td>${escapeHtml(m.email)}</td>
                <td>${escapeHtml(m.phone)}</td>
                <td>${m.joinDate || 'N/A'}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditMemberModal(${m.id})">
                        <i class="bi bi-pencil-square"></i> Edit
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="openDeleteConfirm(${m.id})">
                        <i class="bi bi-trash"></i> Delete
                    </button>
                </td>
            </tr>
        `;
    });
    tableBody.innerHTML = rowsHtml;
}

function filterMembers() {
    const query = document.getElementById("search-input").value.toLowerCase().trim();
    if (!query) {
        renderMembers(membersList);
        return;
    }

    const filtered = membersList.filter(m =>
        m.name.toLowerCase().includes(query) ||
        m.email.toLowerCase().includes(query) ||
        m.phone.toLowerCase().includes(query)
    );
    renderMembers(filtered);
}

function openAddMemberModal() {
    document.getElementById("member-form").reset();
    document.getElementById("member-id").value = "";
    document.getElementById("modalTitle").textContent = "Add New Member";
    document.getElementById("joinDate").value = new Date().toISOString().split('T')[0];
    memberModal.show();
}

function openEditMemberModal(id) {
    const member = membersList.find(m => m.id === id);
    if (!member) return;

    document.getElementById("member-id").value = member.id;
    document.getElementById("name").value = member.name;
    document.getElementById("email").value = member.email;
    document.getElementById("phone").value = member.phone;
    document.getElementById("joinDate").value = member.joinDate;
    document.getElementById("modalTitle").textContent = "Edit Member";
    memberModal.show();
}

async function handleMemberFormSubmit(e) {
    e.preventDefault();

    const id = document.getElementById("member-id").value;
    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const joinDate = document.getElementById("joinDate").value;

    if (!name || !email || !phone || !joinDate) {
        showToast("Please fill in all required fields.", "warning");
        return;
    }

    const memberData = { name, email, phone, joinDate };
    const submitBtn = document.getElementById("save-member-btn");
    submitBtn.disabled = true;
    submitBtn.innerHTML = `<span class="spinner-border spinner-border-sm me-1"></span> Saving...`;

    try {
        if (id) {
            await apiPut(`/members/${id}`, memberData);
            showToast("Member updated successfully.", "success");
        } else {
            await apiPost("/members", memberData);
            showToast("Member added successfully.", "success");
        }
        memberModal.hide();
        loadMembers();
    } catch (error) {
        showToast(error.message, "danger");
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = "Save Member";
    }
}

function openDeleteConfirm(id) {
    deleteMemberId = id;
    deleteModal.show();
}

async function executeDeleteMember() {
    if (!deleteMemberId) return;

    try {
        await apiDelete(`/members/${deleteMemberId}`);
        showToast("Member deleted successfully.", "success");
        deleteModal.hide();
        loadMembers();
    } catch (error) {
        showToast(error.message, "danger");
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}
