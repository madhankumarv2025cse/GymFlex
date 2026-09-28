document.addEventListener("DOMContentLoaded", () => {
    checkAuth();
    loadDashboardStats();
    loadExpiringMemberships();
    setCurrentDate();
});

function setCurrentDate() {
    const dateElement = document.getElementById("current-date");
    if (dateElement) {
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        dateElement.textContent = new Date().toLocaleDateString('en-US', options);
    }
}

async function loadDashboardStats() {
    try {
        const stats = await apiGet("/dashboard");
        if (stats) {
            document.getElementById("total-members").textContent = stats.totalMembers || 0;
            document.getElementById("active-memberships").textContent = stats.activeMemberships || 0;
            document.getElementById("expired-memberships").textContent = stats.expiredMemberships || 0;
            document.getElementById("expiring-memberships").textContent = stats.expiringWithin7Days || 0;
            document.getElementById("monthly-checkins").textContent = stats.totalCheckInsThisMonth || 0;
        }
    } catch (error) {
        showToast(error.message, "danger");
    }
}

async function loadExpiringMemberships() {
    const tableBody = document.getElementById("expiring-table-body");
    const container = document.getElementById("expiring-container");
    if (!tableBody) return;

    try {
        const memberships = await apiGet("/memberships/expiring");

        if (!memberships || memberships.length === 0) {
            container.innerHTML = `
                <div class="text-center py-4 text-muted">
                    <i class="bi bi-shield-check fs-2 text-success d-block mb-2"></i>
                    <p class="mb-0">No memberships expiring within the next 7 days.</p>
                </div>
            `;
            return;
        }

        let rowsHtml = "";
        memberships.forEach(m => {
            rowsHtml += `
                <tr>
                    <td><strong>${m.member ? m.member.name : 'N/A'}</strong><br><small class="text-muted">${m.member ? m.member.email : ''}</small></td>
                    <td><span class="badge bg-light text-dark border">${m.plan ? m.plan.name : 'N/A'}</span></td>
                    <td>${m.startDate || 'N/A'}</td>
                    <td class="fw-semibold text-danger">${m.endDate || 'N/A'}</td>
                    <td><span class="badge badge-expiring">EXPIRING SOON</span></td>
                    <td>
                        <a href="memberships.html?renew=${m.id}" class="btn btn-sm btn-outline-primary">
                            <i class="bi bi-arrow-repeat me-1"></i>Renew
                        </a>
                    </td>
                </tr>
            `;
        });
        tableBody.innerHTML = rowsHtml;
    } catch (error) {
        showToast(error.message, "danger");
    }
}
