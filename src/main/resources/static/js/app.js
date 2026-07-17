document.addEventListener("DOMContentLoaded", () => {
    console.log("Antigravity Shield Frontend Initialized");
    
    // UI Elements
    const themeToggleBtn = document.getElementById("themeToggleBtn");
    const themeIcon = document.getElementById("themeIcon");
    
    const scanForm = document.getElementById("scanForm");
    const targetUrlInput = document.getElementById("targetUrl");
    const scanBtn = document.getElementById("scanBtn");
    
    // View Panels
    const viewPanels = {
        home: document.getElementById("homeView"),
        dashboard: document.getElementById("dashboardView"),
        history: document.getElementById("historyView"),
        report: document.getElementById("reportView")
    };
    
    // Nav Links
    const navLinks = {
        home: document.getElementById("navHome"),
        dashboard: document.getElementById("navDashboard"),
        history: document.getElementById("navHistory"),
        report: document.getElementById("navReport"),
        reportItem: document.getElementById("navReportItem")
    };
    
    const loadingOverlay = document.getElementById("loadingOverlay");
    
    // Dashboard Stats Elements
    const dashTotalScans = document.getElementById("dashTotalScans");
    const dashAvgScore = document.getElementById("dashAvgScore");
    const dashAvgTime = document.getElementById("dashAvgTime");
    const dashHighRisk = document.getElementById("dashHighRisk");
    const dashRiskList = document.getElementById("dashRiskList");
    
    const statCspCount = document.getElementById("statCspCount");
    const statCspBar = document.getElementById("statCspBar");
    const statHstsCount = document.getElementById("statHstsCount");
    const statHstsBar = document.getElementById("statHstsBar");
    const statCookiesCount = document.getElementById("statCookiesCount");
    const statCookiesBar = document.getElementById("statCookiesBar");
    const statSslCount = document.getElementById("statSslCount");
    const statSslBar = document.getElementById("statSslBar");
    
    // History View Elements
    const historyTableBody = document.getElementById("historyTableBody");
    const clearHistoryBtn = document.getElementById("clearHistoryBtn");
    const historySearchInput = document.getElementById("historySearchInput");
    
    // Detailed Report Elements
    const reportBadgeStatus = document.getElementById("reportBadgeStatus");
    const reportIdVal = document.getElementById("reportIdVal");
    const reportTargetUrl = document.getElementById("reportTargetUrl");
    const reportCompletedAt = document.getElementById("reportCompletedAt");
    const reportScoreRing = document.getElementById("reportScoreRing");
    const reportScore = document.getElementById("reportScore");
    
    const reportResponseTime = document.getElementById("reportResponseTime");
    const reportRedirectCount = document.getElementById("reportRedirectCount");
    const reportHttpsStatus = document.getElementById("reportHttpsStatus");
    const reportHttpsIcon = document.getElementById("reportHttpsIcon");
    const reportHeadersRatio = document.getElementById("reportHeadersRatio");
    const reportSslStatus = document.getElementById("reportSslStatus");
    const reportSslIcon = document.getElementById("reportSslIcon");
    const reportCookiesRatio = document.getElementById("reportCookiesRatio");
    
    const reportRecsCard = document.getElementById("reportRecsCard");
    const reportRecsContainer = document.getElementById("reportRecsContainer");
    const reportSslBody = document.getElementById("reportSslBody");
    const reportHeadersTableBody = document.getElementById("reportHeadersTableBody");
    const reportCookiesTableBody = document.getElementById("reportCookiesTableBody");
    
    const reportBackBtn = document.getElementById("reportBackBtn");
    const exportPdfBtn = document.getElementById("exportPdfBtn");
    const exportJsonBtn = document.getElementById("exportJsonBtn");
    const sortDateHeader = document.getElementById("sortDateHeader");
    const sortDateIcon = document.getElementById("sortDateIcon");
    const sortScoreHeader = document.getElementById("sortScoreHeader");
    const sortScoreIcon = document.getElementById("sortScoreIcon");

    let allHistoricalScans = [];
    let activeReportId = null;
    let currentReportScan = null;
    
    // Sort states
    let currentSortField = "date"; // "date" or "score"
    let sortAscending = false;

    // Chart instances
    let scoreDistChartInstance = null;
    let vulnChartInstance = null;
    let trendChartInstance = null;

    // --- 0. TOAST NOTIFICATIONS ---
    const appToastEl = document.getElementById("appToast");
    const toastIcon = document.getElementById("toastIcon");
    const toastTitle = document.getElementById("toastTitle");
    const toastMessage = document.getElementById("toastMessage");
    const appToast = new bootstrap.Toast(appToastEl, { delay: 5000 });

    function showToast(title, message, type = "info") {
        if (!appToastEl) return;
        appToastEl.classList.remove("toast-success", "toast-danger", "toast-warning", "toast-info");
        appToastEl.classList.add(`toast-${type}`);
        
        if (type === "success") {
            toastIcon.className = "bi bi-check-circle-fill text-success fs-5";
        } else if (type === "danger") {
            toastIcon.className = "bi bi-exclamation-octagon-fill text-danger fs-5";
        } else if (type === "warning") {
            toastIcon.className = "bi bi-exclamation-triangle-fill text-warning fs-5";
        } else {
            toastIcon.className = "bi bi-info-circle-fill text-info fs-5";
        }
        
        toastTitle.textContent = title;
        toastMessage.textContent = message;
        appToast.show();
    }

    // --- 1. THEME MANAGER (DARK / LIGHT MODE) ---
    function initTheme() {
        const savedTheme = localStorage.getItem("theme") || "dark";
        document.documentElement.setAttribute("data-theme", savedTheme);
        updateThemeIcon(savedTheme);
    }
    
    function toggleTheme() {
        const currentTheme = document.documentElement.getAttribute("data-theme");
        const newTheme = currentTheme === "dark" ? "light" : "dark";
        document.documentElement.setAttribute("data-theme", newTheme);
        localStorage.setItem("theme", newTheme);
        updateThemeIcon(newTheme);
        
        // Update Chart.js colors on theme switch
        const isDark = newTheme === "dark";
        const textColor = isDark ? "#9ca3af" : "#64748b";
        const gridColor = isDark ? "rgba(255,255,255,0.08)" : "rgba(0,0,0,0.08)";
        
        if (scoreDistChartInstance) {
            scoreDistChartInstance.options.plugins.legend.labels.color = textColor;
            scoreDistChartInstance.data.datasets[0].borderColor = isDark ? "#111827" : "#ffffff";
            scoreDistChartInstance.update();
        }
        if (vulnChartInstance) {
            vulnChartInstance.options.scales.x.ticks.color = textColor;
            vulnChartInstance.options.scales.x.grid.color = gridColor;
            vulnChartInstance.options.scales.y.ticks.color = textColor;
            vulnChartInstance.options.scales.y.grid.color = gridColor;
            vulnChartInstance.update();
        }
        if (trendChartInstance) {
            trendChartInstance.options.scales.x.ticks.color = textColor;
            trendChartInstance.options.scales.x.grid.color = gridColor;
            trendChartInstance.options.scales.y.ticks.color = textColor;
            trendChartInstance.options.scales.y.grid.color = gridColor;
            trendChartInstance.data.datasets[0].pointBorderColor = isDark ? "#111827" : "#ffffff";
            trendChartInstance.update();
        }
    }
    
    function updateThemeIcon(theme) {
        if (theme === "dark") {
            themeIcon.className = "bi bi-sun-fill text-warning fs-5";
        } else {
            themeIcon.className = "bi bi-moon-stars-fill text-primary fs-5";
        }
    }
    
    themeToggleBtn.addEventListener("click", toggleTheme);
    initTheme();

    // --- 2. ROUTER & VIEW SWITCHING ---
    function handleRouting() {
        const hash = window.location.hash || "#home";
        
        // Hide loading
        loadingOverlay.classList.add("d-none");
        
        // Parse route parameters
        let view = "home";
        let paramId = null;
        
        if (hash.startsWith("#report")) {
            view = "report";
            const match = hash.match(/\?id=(\d+)/);
            if (match) {
                paramId = parseInt(match[1], 10);
            }
        } else {
            view = hash.substring(1);
        }
        
        // Ensure valid panel exists
        if (!viewPanels[view]) {
            view = "home";
        }
        
        // Hide all views, display target
        Object.keys(viewPanels).forEach(key => {
            viewPanels[key].classList.add("d-none");
        });
        viewPanels[view].classList.remove("d-none");
        
        // Update nav active tags
        Object.keys(navLinks).forEach(key => {
            if (navLinks[key]) {
                navLinks[key].classList.remove("active");
            }
        });
        if (navLinks[view]) {
            navLinks[view].classList.add("active");
        }
        
        // Route-specific tasks
        if (view === "home") {
            targetUrlInput.value = "";
            targetUrlInput.focus();
        } else if (view === "dashboard") {
            loadDashboardData();
        } else if (view === "history") {
            loadHistoryData();
        } else if (view === "report") {
            if (paramId) {
                activeReportId = paramId;
                navLinks.reportItem.classList.remove("d-none");
                loadReportDetails(paramId);
            } else if (activeReportId) {
                // Return to active report
                window.location.hash = `#report?id=${activeReportId}`;
            } else {
                // If no active report, fall back to home
                window.location.hash = "#home";
            }
        }
    }

    window.addEventListener("hashchange", handleRouting);
    handleRouting(); // First check on startup

    // --- Terminal Log Helper ---
    function logTerminal(message, type = "info") {
        const termBody = document.getElementById("terminalLogBody");
        if (!termBody) return;
        
        const timestamp = new Date().toLocaleTimeString();
        let color = "#a1a1aa"; // default zinc-400
        let prefix = "INFO";
        
        if (type === "success") {
            color = "#10b981"; // emerald-500
            prefix = "SUCCESS";
        } else if (type === "error") {
            color = "#f43f5e"; // rose-500
            prefix = "ERROR";
        } else if (type === "warning") {
            color = "#f59e0b"; // amber-500
            prefix = "WARN";
        } else if (type === "process") {
            color = "#818cf8"; // indigo-400
            prefix = "RUN";
        }
        
        const logLine = document.createElement("div");
        logLine.style.color = color;
        logLine.style.marginBottom = "4px";
        logLine.style.lineHeight = "1.4";
        logLine.innerHTML = `[${timestamp}] [${prefix}] ${message}`;
        
        termBody.appendChild(logLine);
        
        // Auto scroll to bottom
        const parent = termBody.parentElement;
        parent.scrollTop = parent.scrollHeight;
    }

    // --- 3. SUBMIT SCAN ACTION ---
    scanForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const targetUrl = targetUrlInput.value.trim();
        if (!targetUrl) return;

        // Clear previous terminal console contents
        const termBody = document.getElementById("terminalLogBody");
        if (termBody) termBody.innerHTML = "";

        // Display Loading UI
        Object.keys(viewPanels).forEach(key => {
            viewPanels[key].classList.add("d-none");
        });
        loadingOverlay.classList.remove("d-none");

        logTerminal("Initializing asynchronous Web Shield security scan task...", "info");
        logTerminal(`Target destination: ${targetUrl}`, "info");
        logTerminal("Resolving target IP address and checking HTTP/HTTPS connection availability...", "process");

        // Cycle loading text description
        const phases = [
            "Initializing asynchronous security scan task...",
            "Resolving hostname and checking connection availability...",
            "Inspecting SSL certificate protocols and validity ranges...",
            "Scanning target response headers and cookie flags...",
            "Running weighted security scoring rules...",
            "Finalizing database records and report summaries..."
        ];
        let currentPhase = 0;
        const loadingText = document.querySelector("#loadingOverlay p");
        if (loadingText) {
            loadingText.textContent = phases[0];
        }
        
        const phaseInterval = setInterval(() => {
            if (loadingText) {
                currentPhase = (currentPhase + 1) % phases.length;
                loadingText.textContent = phases[currentPhase];
            }
        }, 1200);

        try {
            const response = await fetch("/api/scans", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ targetUrl })
            });

            if (!response.ok) {
                const errData = await response.json().catch(() => ({}));
                const msg = errData.message || "Server error starting scan.";
                throw new Error(msg);
            }

            const scanResult = await response.json();
            const scanId = scanResult.id;
            
            logTerminal(`Security scan task accepted by background engine. Task ID: ${scanId}`, "success");
            logTerminal("Inspecting target SSL certificate protocols, cipher suites, and issuer...", "process");

            let polledCount = 0;
            // Start Polling for Async Scan Status
            await new Promise((resolve, reject) => {
                const pollInterval = setInterval(async () => {
                    polledCount++;
                    try {
                        const statusResponse = await fetch(`/api/scans/${scanId}`);
                        if (!statusResponse.ok) {
                            clearInterval(pollInterval);
                            reject(new Error("Failed to fetch scan progress."));
                            return;
                        }
                        
                        const currentScan = await statusResponse.json();
                        
                        // Print mock-live steps to console
                        if (polledCount === 1) {
                            logTerminal("SSL inspection successful. Querying security headers (CSP, HSTS, X-Frame-Options)...", "success");
                        } else if (polledCount === 2) {
                            logTerminal("Analyzing response cookie attributes (Secure, HttpOnly, SameSite, Expiration)...", "process");
                        } else if (polledCount === 3) {
                            logTerminal("Applying weighted scoring algorithm to security posture metrics...", "process");
                        }

                        if (currentScan.status === "COMPLETED") {
                            clearInterval(pollInterval);
                            logTerminal("Scoring completed. Finalizing database records and report summaries...", "success");
                            logTerminal(`Scan successfully completed! Score: ${currentScan.score}/100`, "success");
                            
                            setTimeout(() => {
                                showToast("Scan Success", `Audit completed successfully for ${targetUrl}`, "success");
                                activeReportId = scanId;
                                window.location.hash = `#report?id=${scanId}`;
                                resolve();
                            }, 500);
                        } else if (currentScan.status === "FAILED") {
                            clearInterval(pollInterval);
                            logTerminal("Scan execution aborted due to server verification failure.", "error");
                            reject(new Error("Scanner failed to analyze the target URL. Please verify host accessibility."));
                        }
                    } catch (pollErr) {
                        clearInterval(pollInterval);
                        logTerminal(`Fatal polling error: ${pollErr.message}`, "error");
                        reject(pollErr);
                    }
                }, 1200);
            });

        } catch (err) {
            logTerminal(`Scan execution failed: ${err.message}`, "error");
            console.error(err);
            showToast("Scan Failed", err.message, "danger");
            window.location.hash = "#home";
        } finally {
            clearInterval(phaseInterval);
        }
    });

    // --- 4. DASHBOARD LOADER & DATA SUMMARY ---
    async function loadDashboardData() {
        try {
            const response = await fetch("/api/scans");
            if (!response.ok) throw new Error("Failed to load historical scans.");
            
            const scans = await response.json();
            allHistoricalScans = scans;
            
            renderDashboardStats(scans);
        } catch (err) {
            console.error(err);
        }
    }

    function renderDashboardStats(scans) {
        const completedScans = scans.filter(s => s.status === "COMPLETED");
        const total = completedScans.length;
        
        dashTotalScans.textContent = total;
        
        if (total === 0) {
            dashAvgScore.textContent = "0";
            dashAvgTime.textContent = "0 ms";
            dashHighRisk.textContent = "0";
            
            dashRiskList.innerHTML = `<div class="p-4 text-center text-muted-custom small">No risk targets scanned yet.</div>`;
            updateStatProgress(0, 0, 0, 0);
            return;
        }

        // Metrics Calculations
        let totalScore = 0;
        let totalTime = 0;
        let highRiskCount = 0;
        
        // Count specific failed features
        let missingCsp = 0;
        let missingHsts = 0;
        let insecureCookies = 0;
        let expiredSsl = 0;

        let secureCount = 0;
        let warningCount = 0;
        let dangerCount = 0;

        completedScans.forEach(scan => {
            totalScore += scan.score;
            totalTime += (scan.responseTimeMs || 0);
            
            if (scan.score >= 80) {
                secureCount++;
            } else if (scan.score >= 55) {
                warningCount++;
            } else {
                dangerCount++;
                highRiskCount++;
            }

            // Evaluate security headers
            if (scan.securityHeaders) {
                const csp = scan.securityHeaders.find(h => h.headerName.toLowerCase() === "content-security-policy");
                const hsts = scan.securityHeaders.find(h => h.headerName.toLowerCase() === "strict-transport-security");
                if (!csp || !csp.present || csp.securityRating !== "SECURE") missingCsp++;
                if (!hsts || !hsts.present || hsts.securityRating !== "SECURE") missingHsts++;
            } else {
                missingCsp++;
                missingHsts++;
            }

            // Cookies
            if (scan.cookieAnalyses && scan.cookieAnalyses.some(c => !c.secure || !c.httpOnly)) {
                insecureCookies++;
            }

            // SSL Expired
            if (scan.sslInfo && (scan.sslInfo.expired || !scan.sslInfo.sslEnabled)) {
                expiredSsl++;
            }
        });

        // Set Values
        dashAvgScore.textContent = Math.round(totalScore / total);
        dashAvgTime.textContent = `${Math.round(totalTime / total)} ms`;
        dashHighRisk.textContent = highRiskCount;

        // Set Progress indicators
        updateStatProgress(
            Math.round((missingCsp / total) * 100),
            Math.round((missingHsts / total) * 100),
            Math.round((insecureCookies / total) * 100),
            Math.round((expiredSsl / total) * 100)
        );

        // Render charts dynamically
        renderDashboardCharts(secureCount, warningCount, dangerCount, missingCsp, missingHsts, insecureCookies, expiredSsl, total);
        renderTrendChart(completedScans);

        // Render High Risk list (Top 5 lowest scores)
        const sortedRisks = [...completedScans]
            .sort((a, b) => a.score - b.score)
            .slice(0, 5);

        dashRiskList.innerHTML = "";
        sortedRisks.forEach(scan => {
            const item = document.createElement("a");
            item.href = `#report?id=${scan.id}`;
            item.className = "list-group-item list-group-item-action d-flex justify-content-between align-items-center py-3 px-4";
            
            let badgeClass = "badge-score-secure";
            if (scan.score < 55) badgeClass = "badge-score-danger";
            else if (scan.score < 80) badgeClass = "badge-score-warning";

            item.innerHTML = `
                <span class="text-truncate max-w-url text-main fw-medium">${scan.targetUrl}</span>
                <span class="badge ${badgeClass} badge-status">${scan.score} / 100</span>
            `;
            dashRiskList.appendChild(item);
        });
    }

    function renderDashboardCharts(secure, warning, danger, csp, hsts, cookies, ssl, total) {
        const scoreCanvas = document.getElementById("scoreDistributionChart");
        const vulnCanvas = document.getElementById("vulnerabilitiesChart");
        
        if (!scoreCanvas || !vulnCanvas) return;

        const isDark = document.documentElement.getAttribute("data-theme") === "dark";
        const textColor = isDark ? "#9ca3af" : "#64748b";
        const gridColor = isDark ? "rgba(255,255,255,0.08)" : "rgba(0,0,0,0.08)";

        // 1. Score Distribution Doughnut Chart
        if (scoreDistChartInstance) {
            scoreDistChartInstance.destroy();
        }
        
        const ctxScore = scoreCanvas.getContext("2d");
        scoreDistChartInstance = new Chart(ctxScore, {
            type: "doughnut",
            data: {
                labels: ["Secure (80-100)", "Warning (55-79)", "High Risk (0-54)"],
                datasets: [{
                    data: [secure, warning, danger],
                    backgroundColor: ["#10b981", "#f59e0b", "#f43f5e"],
                    borderWidth: isDark ? 2 : 1,
                    borderColor: isDark ? "#111827" : "#ffffff"
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: "bottom",
                        labels: {
                            color: textColor,
                            font: { family: "Plus Jakarta Sans", size: 11 }
                        }
                    }
                }
            }
        });

        // 2. Common Vulnerabilities Bar Chart
        if (vulnChartInstance) {
            vulnChartInstance.destroy();
        }

        const ctxVuln = vulnCanvas.getContext("2d");
        vulnChartInstance = new Chart(ctxVuln, {
            type: "bar",
            data: {
                labels: ["Missing CSP", "Missing HSTS", "Insecure Cookies", "SSL Issues"],
                datasets: [{
                    label: "Failed Checks",
                    data: [csp, hsts, cookies, ssl],
                    backgroundColor: ["#818cf8", "#f43f5e", "#ec4899", "#f59e0b"],
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    x: {
                        grid: {
                            color: gridColor
                        },
                        ticks: {
                            color: textColor,
                            font: { family: "Plus Jakarta Sans", size: 11 }
                        }
                    },
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: gridColor
                        },
                        ticks: {
                            color: textColor,
                            font: { family: "Plus Jakarta Sans", size: 11 },
                            stepSize: 1
                        }
                    }
                }
            }
        });
    }

    function renderTrendChart(completedScans) {
        const trendCanvas = document.getElementById("scoreHistoryTrendChart");
        if (!trendCanvas) return;

        const isDark = document.documentElement.getAttribute("data-theme") === "dark";
        const textColor = isDark ? "#9ca3af" : "#64748b";
        const gridColor = isDark ? "rgba(255,255,255,0.08)" : "rgba(0,0,0,0.08)";

        if (trendChartInstance) {
            trendChartInstance.destroy();
        }

        // Sort scans chronologically by creation date
        const sortedScans = [...completedScans].sort((a, b) => new Date(a.createdAt || a.completedAt) - new Date(b.createdAt || b.completedAt));
        
        // Take last 15 scans to avoid overcrowding the line chart
        const recentScans = sortedScans.slice(-15);

        const ctxTrend = trendCanvas.getContext("2d");
        trendChartInstance = new Chart(ctxTrend, {
            type: "line",
            data: {
                labels: recentScans.map(scan => formatDateTime(scan.createdAt || scan.completedAt)),
                datasets: [{
                    label: "Website Security Score",
                    data: recentScans.map(scan => scan.score),
                    borderColor: "#6366f1",
                    backgroundColor: "rgba(99, 102, 241, 0.1)",
                    borderWidth: 2,
                    tension: 0.3,
                    fill: true,
                    pointBackgroundColor: "#818cf8",
                    pointBorderColor: isDark ? "#111827" : "#ffffff",
                    pointRadius: 4,
                    pointHoverRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            title: function(context) {
                                const idx = context[0].dataIndex;
                                return `URL: ${recentScans[idx].targetUrl}`;
                            },
                            label: function(context) {
                                return `Score: ${context.parsed.y}/100`;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        grid: {
                            color: gridColor
                        },
                        ticks: {
                            color: textColor,
                            font: { family: "Plus Jakarta Sans", size: 10 },
                            maxRotation: 45,
                            minRotation: 45
                        }
                    },
                    y: {
                        min: 0,
                        max: 100,
                        grid: {
                            color: gridColor
                        },
                        ticks: {
                            color: textColor,
                            font: { family: "Plus Jakarta Sans", size: 11 },
                            stepSize: 20
                        }
                    }
                }
            }
        });
    }

    function updateStatProgress(cspPercent, hstsPercent, cookiePercent, sslPercent) {
        statCspCount.textContent = `${cspPercent}%`;
        statCspBar.style.width = `${cspPercent}%`;
        
        statHstsCount.textContent = `${hstsPercent}%`;
        statHstsBar.style.width = `${hstsPercent}%`;
        
        statCookiesCount.textContent = `${cookiePercent}%`;
        statCookiesBar.style.width = `${cookiePercent}%`;
        
        statSslCount.textContent = `${sslPercent}%`;
        statSslBar.style.width = `${sslPercent}%`;
    }

    // --- 5. HISTORY AUDIT TABLE ---
    async function loadHistoryData() {
        try {
            const response = await fetch("/api/scans");
            if (!response.ok) throw new Error("Failed to load historical scans.");
            
            const scans = await response.json();
            allHistoricalScans = scans;
            
            sortScans();
            updateSortIcons();
            
            renderHistoryTable(allHistoricalScans);
        } catch (err) {
            console.error(err);
        }
    }

    function renderHistoryTable(scans) {
        historyTableBody.innerHTML = "";
        if (scans.length === 0) {
            historyTableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center py-4 text-muted-custom">No scans conducted yet. Submit a URL on the home view to begin.</td>
                </tr>
            `;
            return;
        }

        scans.forEach(scan => {
            const tr = document.createElement("tr");
            
            const dateStr = formatDateTime(scan.createdAt);
            const timeStr = scan.responseTimeMs ? `${scan.responseTimeMs} ms` : "N/A";
            
            let statusBadge = `<span class="badge bg-success-light text-success badge-status">COMPLETED</span>`;
            if (scan.status === "FAILED") {
                statusBadge = `<span class="badge bg-danger-light text-danger badge-status">FAILED</span>`;
            } else if (scan.status === "IN_PROGRESS") {
                statusBadge = `<span class="badge bg-warning-light text-warning badge-status">IN PROGRESS</span>`;
            }

            let scoreBadge = "N/A";
            if (scan.status === "COMPLETED") {
                let badgeClass = "badge-score-secure";
                if (scan.score < 55) badgeClass = "badge-score-danger";
                else if (scan.score < 80) badgeClass = "badge-score-warning";
                scoreBadge = `<span class="badge ${badgeClass} badge-status">${scan.score} / 100</span>`;
            }

            tr.innerHTML = `
                <td class="fw-medium text-main text-truncate max-w-url">${scan.targetUrl}</td>
                <td>${dateStr}</td>
                <td>${timeStr}</td>
                <td>${scan.redirectCount}</td>
                <td>${scoreBadge}</td>
                <td>${statusBadge}</td>
                <td class="text-end">
                    <div class="d-flex justify-content-end gap-2">
                        <a href="#report?id=${scan.id}" class="btn btn-primary btn-sm px-3">
                            <i class="bi bi-file-earmark-text"></i>
                        </a>
                        <button class="btn btn-outline-danger btn-sm delete-scan-btn" data-id="${scan.id}">
                            <i class="bi bi-trash3"></i>
                        </button>
                    </div>
                </td>
            `;
            historyTableBody.appendChild(tr);
        });

        // Attach action events
        document.querySelectorAll(".delete-scan-btn").forEach(btn => {
            btn.addEventListener("click", async (e) => {
                const scanId = btn.getAttribute("data-id");
                if (confirm("Are you sure you want to delete this scan history record?")) {
                    await deleteScanHistory(scanId);
                }
            });
        });
    }

    async function deleteScanHistory(id) {
        try {
            const response = await fetch(`/api/scans/${id}`, {
                method: "DELETE"
            });
            if (!response.ok) throw new Error("Delete failed.");
            
            // Reload
            loadHistoryData();
            
            // Clear active report if active was deleted
            if (activeReportId === parseInt(id, 10)) {
                activeReportId = null;
                navLinks.reportItem.classList.add("d-none");
            }
        } catch (err) {
            console.error(err);
            showToast("Delete Failed", "Could not delete historical scan history record.", "danger");
        }
    }

    clearHistoryBtn.addEventListener("click", async () => {
        if (confirm("WARNING: This will permanently erase all historical scans. Proceed?")) {
            for (let scan of allHistoricalScans) {
                try {
                    await fetch(`/api/scans/${scan.id}`, { method: "DELETE" });
                } catch(e){}
            }
            activeReportId = null;
            navLinks.reportItem.classList.add("d-none");
            loadHistoryData();
        }
    });

    // History filter search keyup
    historySearchInput.addEventListener("keyup", () => {
        const query = historySearchInput.value.toLowerCase().trim();
        const filtered = allHistoricalScans.filter(scan => scan.targetUrl.toLowerCase().includes(query));
        renderHistoryTable(filtered);
    });

    // Sorting Logic
    function sortScans() {
        allHistoricalScans.sort((a, b) => {
            let valA, valB;
            if (currentSortField === "date") {
                valA = new Date(a.createdAt || 0).getTime();
                valB = new Date(b.createdAt || 0).getTime();
            } else if (currentSortField === "score") {
                valA = a.score || 0;
                valB = b.score || 0;
            }
            
            if (sortAscending) {
                return valA - valB;
            } else {
                return valB - valA;
            }
        });
    }

    function updateSortIcons() {
        if (!sortDateIcon || !sortScoreIcon) return;
        
        sortDateIcon.className = "bi bi-arrow-down-up ms-1 text-muted";
        sortScoreIcon.className = "bi bi-arrow-down-up ms-1 text-muted";

        if (currentSortField === "date") {
            sortDateIcon.className = sortAscending ? "bi bi-arrow-up ms-1 text-primary" : "bi bi-arrow-down ms-1 text-primary";
        } else if (currentSortField === "score") {
            sortScoreIcon.className = sortAscending ? "bi bi-arrow-up ms-1 text-primary" : "bi bi-arrow-down ms-1 text-primary";
        }
    }

    sortDateHeader.addEventListener("click", () => {
        if (currentSortField === "date") {
            sortAscending = !sortAscending;
        } else {
            currentSortField = "date";
            sortAscending = false;
        }
        sortScans();
        updateSortIcons();
        renderHistoryTable(allHistoricalScans);
    });

    sortScoreHeader.addEventListener("click", () => {
        if (currentSortField === "score") {
            sortAscending = !sortAscending;
        } else {
            currentSortField = "score";
            sortAscending = false;
        }
        sortScans();
        updateSortIcons();
        renderHistoryTable(allHistoricalScans);
    });

    // Export PDF
    exportPdfBtn.addEventListener("click", () => {
        if (currentReportScan && currentReportScan.id) {
            const exRecs = document.getElementById("pdfExcludeRecs").checked;
            const exSsl = document.getElementById("pdfExcludeSsl").checked;
            const exHeaders = document.getElementById("pdfExcludeHeaders").checked;
            const exCookies = document.getElementById("pdfExcludeCookies").checked;
            
            const url = `/api/scans/${currentReportScan.id}/export/pdf` +
                `?excludeRecommendations=${exRecs}` +
                `&excludeSsl=${exSsl}` +
                `&excludeHeaders=${exHeaders}` +
                `&excludeCookies=${exCookies}`;
            window.location.href = url;
        }
    });

    // Export JSON
    exportJsonBtn.addEventListener("click", () => {
        if (currentReportScan) {
            const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(currentReportScan, null, 2));
            const dlAnchorElem = document.createElement('a');
            dlAnchorElem.setAttribute("href", dataStr);
            dlAnchorElem.setAttribute("download", `security-report-${currentReportScan.id}.json`);
            dlAnchorElem.click();
        }
    });

    // --- 6. REPORT RENDERING ---
    async function loadReportDetails(id) {
        try {
            const response = await fetch(`/api/scans/${id}`);
            if (!response.ok) throw new Error("Detailed report not found.");
            
            const scan = await response.json();
            currentReportScan = scan;
            renderReport(scan);
        } catch (err) {
            console.error(err);
            showToast("Report Error", "Detailed scan report could not be loaded.", "danger");
            window.location.hash = "#history";
        }
    }

    function renderReport(scan) {
        reportIdVal.textContent = `#${scan.id}`;
        reportTargetUrl.textContent = scan.targetUrl;
        reportCompletedAt.textContent = formatDateTime(scan.completedAt || scan.createdAt);
        reportScore.textContent = scan.score;

        let statusClass = "bg-success text-white";
        if (scan.status === "FAILED") statusClass = "bg-danger text-white";
        else if (scan.status === "IN_PROGRESS") statusClass = "bg-warning text-dark";
        reportBadgeStatus.className = `badge ${statusClass} badge-status`;

        // Radial Score Gauge Calculation
        const radius = 54;
        const circumference = 2 * Math.PI * radius; // 339.29
        const dashoffset = circumference - (circumference * scan.score) / 100;
        
        reportScoreRing.style.strokeDasharray = circumference;
        reportScoreRing.style.strokeDashoffset = dashoffset;
        
        // Gauge colors based on score
        if (scan.score >= 80) {
            reportScoreRing.style.stroke = "var(--success)";
            reportScore.style.color = "var(--success)";
        } else if (scan.score >= 55) {
            reportScoreRing.style.stroke = "var(--warning)";
            reportScore.style.color = "var(--warning)";
        } else {
            reportScoreRing.style.stroke = "var(--danger)";
            reportScore.style.color = "var(--danger)";
        }

        // Overview boxes
        reportResponseTime.textContent = scan.responseTimeMs ? `${scan.responseTimeMs} ms` : "N/A";
        reportRedirectCount.textContent = scan.redirectCount;
        
        const isSecureHttps = scan.targetUrl.toLowerCase().startsWith("https://");
        reportHttpsStatus.textContent = isSecureHttps ? "Secure (HTTPS)" : "Insecure (HTTP)";
        reportHttpsIcon.className = isSecureHttps ? "bi bi-lock-fill stat-icon text-success" : "bi bi-unlock-fill stat-icon text-danger";

        // Security recommendations rendering
        if (scan.recommendations && scan.recommendations.length > 0) {
            reportRecsCard.classList.remove("d-none");
            reportRecsContainer.innerHTML = "";
            scan.recommendations.forEach(rec => {
                let catClass = rec.category ? rec.category.toLowerCase() : "default";
                let iconClass = "bi-exclamation-triangle";
                
                if (catClass === "https" || catClass === "ssl") {
                    iconClass = "bi-shield-fill-x text-rating-danger";
                } else if (catClass === "cookies") {
                    iconClass = "bi-cookie text-rating-warning";
                } else if (catClass === "redirects") {
                    iconClass = "bi-arrow-right-short text-rating-danger";
                } else if (catClass === "performance") {
                    iconClass = "bi-speedometer text-info";
                } else if (catClass === "headers") {
                    iconClass = "bi-shield-fill-exclamation text-rating-warning";
                }

                const item = document.createElement("div");
                item.className = "list-group-item rec-item";
                item.innerHTML = `
                    <span class="rec-category rec-cat-${catClass}">${rec.category || 'General'}</span>
                    <div class="rec-title">
                        <i class="bi ${iconClass}"></i>${rec.checkName}
                    </div>
                    <div class="rec-desc">${rec.recommendation}</div>
                `;
                reportRecsContainer.appendChild(item);
            });
        } else {
            reportRecsCard.classList.add("d-none");
        }

        // SSL Certificate details
        const isSsl = scan.sslInfo && scan.sslInfo.sslEnabled;
        reportSslStatus.textContent = isSsl ? (scan.sslInfo.expired ? "Expired" : "Active") : "Disabled";
        reportSslIcon.className = isSsl ? (scan.sslInfo.expired ? "bi bi-patch-exclamation stat-icon text-danger" : "bi bi-patch-check stat-icon text-success") : "bi bi-patch-minus stat-icon text-muted";

        if (isSsl) {
            const ssl = scan.sslInfo;
            const validFrom = ssl.validFrom ? formatDateTime(ssl.validFrom) : "N/A";
            const validTo = ssl.validTo ? formatDateTime(ssl.validTo) : "N/A";
            
            let statusBadge = `<span class="badge bg-success-light text-success border border-success">Valid</span>`;
            if (ssl.expired) {
                statusBadge = `<span class="badge bg-danger-light text-danger border border-danger">Expired</span>`;
            }

            reportSslBody.innerHTML = `
                <div class="ssl-grid">
                    <div class="ssl-item">
                        <div class="ssl-label">Status</div>
                        <div class="ssl-value">${statusBadge}</div>
                    </div>
                    <div class="ssl-item">
                        <div class="ssl-label">Protocol Version</div>
                        <div class="ssl-value">${ssl.protocol || 'N/A'}</div>
                    </div>
                    <div class="ssl-item">
                        <div class="ssl-label">Cipher Suite</div>
                        <div class="ssl-value">${ssl.cipherSuite || 'N/A'}</div>
                    </div>
                    <div class="ssl-item">
                        <div class="ssl-label">Issuer Authority</div>
                        <div class="ssl-value">${ssl.issuer || 'N/A'}</div>
                    </div>
                    <div class="ssl-item">
                        <div class="ssl-label">Valid From</div>
                        <div class="ssl-value">${validFrom}</div>
                    </div>
                    <div class="ssl-item">
                        <div class="ssl-label">Expires On</div>
                        <div class="ssl-value">${validTo}</div>
                    </div>
                </div>
            `;
        } else {
            reportSslBody.innerHTML = `
                <div class="text-center text-danger py-2 small">
                    <i class="bi bi-shield-slash-fill me-2 fs-5"></i>
                    No secure SSL/TLS connection could be established. Communication is unencrypted.
                </div>
            `;
        }

        // Security headers table
        reportHeadersTableBody.innerHTML = "";
        let presentHeadersCount = 0;
        if (scan.securityHeaders && scan.securityHeaders.length > 0) {
            scan.securityHeaders.forEach(header => {
                const tr = document.createElement("tr");
                if (header.present) presentHeadersCount++;

                let presentBadge = header.present 
                    ? `<span class="badge badge-header-present badge-status">FOUND</span>`
                    : `<span class="badge badge-header-missing badge-status">MISSING</span>`;
                
                let ratingClass = "text-rating-danger";
                if (header.securityRating === "SECURE") ratingClass = "text-rating-secure";
                else if (header.securityRating === "WARNING") ratingClass = "text-rating-warning";

                tr.innerHTML = `
                    <td class="fw-medium text-main">${header.headerName}</td>
                    <td>${presentBadge}</td>
                    <td class="text-truncate max-w-url">${header.headerValue || '<span class="text-muted-custom">None</span>'}</td>
                    <td class="fw-bold ${ratingClass}">${header.securityRating}</td>
                    <td class="small text-muted-custom">${header.recommendation || 'No issues found.'}</td>
                `;
                reportHeadersTableBody.appendChild(tr);
            });
            reportHeadersRatio.textContent = `${presentHeadersCount} / ${scan.securityHeaders.length}`;
        } else {
            reportHeadersTableBody.innerHTML = `<tr><td colspan="5" class="text-center text-muted-custom py-3">No headers evaluated.</td></tr>`;
            reportHeadersRatio.textContent = "0 / 5";
        }

        // Cookie flags table
        reportCookiesTableBody.innerHTML = "";
        let vulnerableCookiesCount = 0;
        if (scan.cookieAnalyses && scan.cookieAnalyses.length > 0) {
            scan.cookieAnalyses.forEach(cookie => {
                const tr = document.createElement("tr");
                const isInsecure = !cookie.secure || !cookie.httpOnly;
                if (isInsecure) vulnerableCookiesCount++;

                const secureBadge = cookie.secure
                    ? `<span class="badge bg-success-light text-success badge-status">YES</span>`
                    : `<span class="badge bg-danger-light text-danger badge-status">NO</span>`;

                const httpOnlyBadge = cookie.httpOnly
                    ? `<span class="badge bg-success-light text-success badge-status">YES</span>`
                    : `<span class="badge bg-danger-light text-danger badge-status">NO</span>`;

                let riskBadge = `<span class="badge bg-success-light text-success badge-status">SECURE</span>`;
                if (!cookie.secure && !cookie.httpOnly) {
                    riskBadge = `<span class="badge bg-danger-light text-danger badge-status">HIGH RISK</span>`;
                } else if (!cookie.secure || !cookie.httpOnly) {
                    riskBadge = `<span class="badge bg-warning-light text-warning badge-status">MEDIUM RISK</span>`;
                }

                tr.innerHTML = `
                    <td class="fw-medium text-main text-truncate max-w-url">${cookie.cookieName}</td>
                    <td>${secureBadge}</td>
                    <td>${httpOnlyBadge}</td>
                    <td><span class="badge-samesite">${cookie.sameSite || 'None'}</span></td>
                    <td>${cookie.expired ? '<span class="text-danger">Expired</span>' : '<span class="text-success">Active</span>'}</td>
                    <td>${riskBadge}</td>
                `;
                reportCookiesTableBody.appendChild(tr);
            });
            reportCookiesRatio.textContent = vulnerableCookiesCount;
        } else {
            reportCookiesTableBody.innerHTML = `<tr><td colspan="6" class="text-center text-muted-custom py-3">No cookies found in response.</td></tr>`;
            reportCookiesRatio.textContent = "0";
        }
    }

    reportBackBtn.addEventListener("click", () => {
        window.location.hash = "#history";
    });

    // --- 7. HELPER UTILS ---
    function formatDateTime(dateTimeString) {
        if (!dateTimeString) return "N/A";
        try {
            const date = new Date(dateTimeString);
            return date.toLocaleDateString("en-US", {
                year: "numeric",
                month: "short",
                day: "numeric",
                hour: "2-digit",
                minute: "2-digit"
            });
        } catch (e) {
            return dateTimeString;
        }
    }
});
