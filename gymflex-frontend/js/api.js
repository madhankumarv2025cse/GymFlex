const API_BASE_URL = "http://localhost:8080/api";

// Check login status
function checkAuth() {
    const isLoginPage = window.location.pathname.endsWith("index.html") || window.location.pathname.endsWith("/");
    const isLoggedIn = localStorage.getItem("gymflexLoggedIn") === "true";

    if (!isLoggedIn && !isLoginPage) {
        window.location.href = "index.html";
    } else if (isLoggedIn && isLoginPage) {
        window.location.href = "dashboard.html";
    }
}

// Logout function
function logout() {
    localStorage.removeItem("gymflexLoggedIn");
    window.location.href = "index.html";
}

// Toast notification helper
function showToast(message, type = "success") {
    let container = document.getElementById("toast-container");
    if (!container) {
        container = document.createElement("div");
        container.id = "toast-container";
        document.body.appendChild(container);
    }

    const toastId = "toast-" + Date.now();
    const bgClass = type === "success" ? "bg-success text-white" : (type === "warning" ? "bg-warning text-dark" : "bg-danger text-white");
    const icon = type === "success" ? "bi-check-circle-fill" : (type === "warning" ? "bi-exclamation-triangle-fill" : "bi-x-circle-fill");

    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center ${bgClass} border-0 mb-2 shadow" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body d-flex align-items-center">
                    <i class="bi ${icon} fs-5 me-2"></i>
                    <span>${message}</span>
                </div>
                <button type="button" class="btn-close ${type === "warning" ? "" : "btn-close-white"} me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

    container.insertAdjacentHTML("beforeend", toastHtml);
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, { delay: 4000 });
    toast.show();

    toastElement.addEventListener("hidden.bs.toast", () => {
        toastElement.remove();
    });
}

// Generic Fetch Wrappers
async function handleResponse(response) {
    if (!response.ok) {
        let errorMessage = `Error: ${response.status} ${response.statusText}`;
        try {
            const errorData = await response.json();
            if (errorData.message) {
                errorMessage = errorData.message;
            } else if (errorData.errors) {
                errorMessage = Object.values(errorData.errors).join(", ");
            }
        } catch (e) {
            // Non-JSON error body
        }
        throw new Error(errorMessage);
    }

    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
        return await response.json();
    }
    return null;
}

async function apiGet(endpoint) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`);
        return await handleResponse(response);
    } catch (error) {
        if (error.message.includes("Failed to fetch") || error.message.includes("NetworkError")) {
            throw new Error("Unable to connect to GymFlex server. Please make sure the Spring Boot application is running on port 8080.");
        }
        throw error;
    }
}

async function apiPost(endpoint, data = {}) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });
        return await handleResponse(response);
    } catch (error) {
        if (error.message.includes("Failed to fetch") || error.message.includes("NetworkError")) {
            throw new Error("Unable to connect to GymFlex server. Please make sure the Spring Boot application is running on port 8080.");
        }
        throw error;
    }
}

async function apiPut(endpoint, data = {}) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });
        return await handleResponse(response);
    } catch (error) {
        if (error.message.includes("Failed to fetch") || error.message.includes("NetworkError")) {
            throw new Error("Unable to connect to GymFlex server. Please make sure the Spring Boot application is running on port 8080.");
        }
        throw error;
    }
}

async function apiDelete(endpoint) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: "DELETE"
        });
        return await handleResponse(response);
    } catch (error) {
        if (error.message.includes("Failed to fetch") || error.message.includes("NetworkError")) {
            throw new Error("Unable to connect to GymFlex server. Please make sure the Spring Boot application is running on port 8080.");
        }
        throw error;
    }
}
