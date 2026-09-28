let plansList = [];
let deletePlanId = null;
let planModal = null;
let deletePlanModal = null;

document.addEventListener("DOMContentLoaded", () => {
    checkAuth();
    planModal = new bootstrap.Modal(document.getElementById("planModal"));
    deletePlanModal = new bootstrap.Modal(document.getElementById("deletePlanModal"));

    loadPlans();

    document.getElementById("plan-form").addEventListener("submit", handlePlanFormSubmit);
    document.getElementById("confirm-delete-plan-btn").addEventListener("click", executeDeletePlan);
});

async function loadPlans() {
    const container = document.getElementById("plans-container");
    container.innerHTML = `<div class="col-12 text-center py-5"><div class="spinner-border text-primary" role="status"></div><p class="mt-2 text-muted">Loading plans...</p></div>`;

    try {
        const plans = await apiGet("/plans");
        plansList = plans || [];
        renderPlans(plansList);
    } catch (error) {
        container.innerHTML = `<div class="col-12 text-center py-5 text-danger"><i class="bi bi-exclamation-triangle fs-3 d-block mb-2"></i>Unable to load plans.</div>`;
        showToast(error.message, "danger");
    }
}

function renderPlans(list) {
    const container = document.getElementById("plans-container");
    if (!list || list.length === 0) {
        container.innerHTML = `<div class="col-12 text-center py-5 text-muted"><i class="bi bi-journal-x fs-3 d-block mb-2"></i>No membership plans available.</div>`;
        return;
    }

    let cardsHtml = "";
    list.forEach(p => {
        cardsHtml += `
            <div class="col-12 col-md-6 col-lg-4">
                <div class="card stat-card border-0 shadow-sm rounded-3 h-100 p-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="badge bg-primary bg-opacity-10 text-primary fs-6 px-3 py-2 fw-bold text-uppercase">${escapeHtml(p.name)}</span>
                        <div class="dropdown">
                            <button class="btn btn-sm btn-light rounded-circle" type="button" data-bs-toggle="dropdown">
                                <i class="bi bi-three-dots-vertical"></i>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end border-0 shadow">
                                <li><a class="dropdown-item" href="#" onclick="openEditPlanModal(${p.id})"><i class="bi bi-pencil me-2"></i>Edit Plan</a></li>
                                <li><a class="dropdown-item text-danger" href="#" onclick="openDeletePlanConfirm(${p.id})"><i class="bi bi-trash me-2"></i>Delete Plan</a></li>
                            </ul>
                        </div>
                    </div>
                    <h2 class="fw-extrabold text-dark mb-1">₹${p.price}</h2>
                    <p class="text-muted small mb-4"><i class="bi bi-clock-history me-1"></i>Duration: ${p.durationInMonths} ${p.durationInMonths === 1 ? 'Month' : 'Months'}</p>
                    <div class="border-top pt-3 mt-auto">
                        <button class="btn btn-outline-primary btn-sm w-100 fw-semibold" onclick="openEditPlanModal(${p.id})">
                            Edit Details
                        </button>
                    </div>
                </div>
            </div>
        `;
    });
    container.innerHTML = cardsHtml;
}

function openAddPlanModal() {
    document.getElementById("plan-form").reset();
    document.getElementById("plan-id").value = "";
    document.getElementById("planModalTitle").textContent = "Add New Membership Plan";
    planModal.show();
}

function openEditPlanModal(id) {
    const plan = plansList.find(p => p.id === id);
    if (!plan) return;

    document.getElementById("plan-id").value = plan.id;
    document.getElementById("planName").value = plan.name;
    document.getElementById("durationInMonths").value = plan.durationInMonths;
    document.getElementById("price").value = plan.price;
    document.getElementById("planModalTitle").textContent = "Edit Membership Plan";
    planModal.show();
}

async function handlePlanFormSubmit(e) {
    e.preventDefault();

    const id = document.getElementById("plan-id").value;
    const name = document.getElementById("planName").value.trim();
    const durationInMonths = parseInt(document.getElementById("durationInMonths").value);
    const price = parseFloat(document.getElementById("price").value);

    if (!name || isNaN(durationInMonths) || isNaN(price) || durationInMonths <= 0 || price <= 0) {
        showToast("Please provide valid plan details.", "warning");
        return;
    }

    const planData = { name, durationInMonths, price };
    const submitBtn = document.getElementById("save-plan-btn");
    submitBtn.disabled = true;

    try {
        if (id) {
            await apiPut(`/plans/${id}`, planData);
            showToast("Plan updated successfully.", "success");
        } else {
            await apiPost("/plans", planData);
            showToast("Plan created successfully.", "success");
        }
        planModal.hide();
        loadPlans();
    } catch (error) {
        showToast(error.message, "danger");
    } finally {
        submitBtn.disabled = false;
    }
}

function openDeletePlanConfirm(id) {
    deletePlanId = id;
    deletePlanModal.show();
}

async function executeDeletePlan() {
    if (!deletePlanId) return;

    try {
        await apiDelete(`/plans/${deletePlanId}`);
        showToast("Plan deleted successfully.", "success");
        deletePlanModal.hide();
        loadPlans();
    } catch (error) {
        showToast(error.message, "danger");
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}
