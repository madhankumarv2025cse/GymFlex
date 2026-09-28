let membersList = [];

document.addEventListener("DOMContentLoaded", () => {
    checkAuth();
    loadMemberOptions();

    document.getElementById("checkin-form").addEventListener("submit", handleCheckInSubmit);
    document.getElementById("historyMemberSelect").addEventListener("change", handleHistoryMemberChange);
});

async function loadMemberOptions() {
    try {
        membersList = await apiGet("/members") || [];

        const checkinSelect = document.getElementById("checkinMemberSelect");
        const historySelect = document.getElementById("historyMemberSelect");

        let checkinOptions = `<option value="">-- Select Member to Check In --</option>`;
        let historyOptions = `<option value="">-- Select Member to View History --</option>`;

        membersList.forEach(m => {
            const opt = `<option value="${m.id}">${escapeHtml(m.name)} (${escapeHtml(m.phone)})</option>`;
            checkinOptions += opt;
            historyOptions += opt;
        });

        checkinSelect.innerHTML = checkinOptions;
        historySelect.innerHTML = historyOptions;

        if (membersList.length > 0) {
            historySelect.value = membersList[0].id;
            loadMemberHistoryAndCount(membersList[0].id);
        }
    } catch (error) {
        showToast("Unable to load members for attendance check-in.", "danger");
    }
}

async function handleCheckInSubmit(e) {
    e.preventDefault();

    const memberId = document.getElementById("checkinMemberSelect").value;
    if (!memberId) {
        showToast("Please select a member to check in.", "warning");
        return;
    }

    const submitBtn = document.getElementById("checkin-btn");
    submitBtn.disabled = true;

    try {
        await apiPost(`/checkins/member/${memberId}`);
        showToast("Check-in recorded successfully.", "success");
        document.getElementById("checkinMemberSelect").value = "";

        // Refresh history if same member selected in history
        const historyMemberId = document.getElementById("historyMemberSelect").value;
        if (historyMemberId && historyMemberId === memberId) {
            loadMemberHistoryAndCount(memberId);
        }
    } catch (error) {
        // Display exact backend error e.g. "Check-in rejected. Membership has expired."
        showToast(error.message, "danger");
    } finally {
        submitBtn.disabled = false;
    }
}

function handleHistoryMemberChange() {
    const memberId = document.getElementById("historyMemberSelect").value;
    if (memberId) {
        loadMemberHistoryAndCount(memberId);
    } else {
        document.getElementById("monthly-count-display").textContent = "0 Visits";
        document.getElementById("monthly-count-subtitle").textContent = "Select a member to view records";
        document.getElementById("attendance-table-body").innerHTML = `<tr><td colspan="3" class="text-center py-4 text-muted">Select a member above.</td></tr>`;
    }
}

async function loadMemberHistoryAndCount(memberId) {
    const tableBody = document.getElementById("attendance-table-body");
    tableBody.innerHTML = `<tr><td colspan="3" class="text-center py-4"><div class="spinner-border text-primary spinner-border-sm"></div><p class="mt-2 text-muted mb-0 small">Loading history...</p></td></tr>`;

    try {
        // Load monthly count
        const monthlyData = await apiGet(`/checkins/member/${memberId}/monthly-count`);
        if (monthlyData) {
            document.getElementById("monthly-count-display").textContent = `${monthlyData.attendanceCount} Visits`;
            document.getElementById("monthly-count-subtitle").textContent = `For ${monthlyData.month} ${monthlyData.year} (${monthlyData.memberName})`;
        }

        // Load check-in records list
        const checkIns = await apiGet(`/checkins/member/${memberId}`);
        renderAttendanceHistory(checkIns);
    } catch (error) {
        tableBody.innerHTML = `<tr><td colspan="3" class="text-center py-4 text-danger">Unable to load attendance history.</td></tr>`;
        showToast(error.message, "danger");
    }
}

function renderAttendanceHistory(list) {
    const tableBody = document.getElementById("attendance-table-body");
    if (!list || list.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="3" class="text-center py-4 text-muted"><i class="bi bi-calendar-x fs-3 d-block mb-2"></i>No attendance records found for this member.</td></tr>`;
        return;
    }

    let rowsHtml = "";
    list.forEach(c => {
        rowsHtml += `
            <tr>
                <td>#${c.id}</td>
                <td class="fw-semibold">${c.member ? escapeHtml(c.member.name) : 'N/A'}</td>
                <td><i class="bi bi-calendar3 me-2 text-primary"></i>${c.checkInDate || 'N/A'}</td>
            </tr>
        `;
    });
    tableBody.innerHTML = rowsHtml;
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}
