document.addEventListener('DOMContentLoaded', () => {
    let currentStep = 1;
    let selectedVendor = null;
    window.fetchedData = null; 

    const BASE_URL = "http://localhost:8080/tradeshowproject"; 

    const modal = document.getElementById('migrationModal');
    const startBtn = document.getElementById('startMigrationBtn');
    const nextBtn = document.getElementById('nextBtn'
		
	);
    const backBtn = document.getElementById('backBtn');
    const closeModal = document.getElementById('closeModal');

    
    startBtn.onclick = () => {
        modal.classList.add('active');
        currentStep = 1;
        updateStepDisplay();
    };

    if (closeModal) {
        closeModal.onclick = () => {
            modal.classList.remove('active');
        };
    }

    document.querySelectorAll('.vendor-card').forEach(card => {
        card.addEventListener('click', () => {
            document.querySelectorAll('.vendor-card').forEach(c => c.classList.remove('selected'));
            card.classList.add('selected');
            selectedVendor = card.dataset.vendor;
        });
    });

    async function apiFetch(endpoint, payload) {
        const response = await fetch(`${BASE_URL}/${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!response.ok) throw new Error(await response.text());
        return await response.json();
    }

    nextBtn.onclick = async () => {
        try {
            if (currentStep === 1) {
                
                const dcElement = document.getElementById('dcRegion');
                const dcValue = dcElement ? dcElement.value : "in";
                
                console.log("DEBUG: Step 1 - Captured DC Value:", dcValue);

                const payload = {
                    dc_region: dcValue, 
                    client_id: document.getElementById('clientId').value,
                    client_secret: document.getElementById('clientSecret').value,
                    refresh_token: document.getElementById('refreshToken').value
                };

                console.log("DEBUG: Step 1 - Sending Payload:", JSON.stringify(payload));
                
                await apiFetch('AuthSite24x7', payload);
                currentStep = 2;
            } 
            else if (currentStep === 2) {
                if (!selectedVendor) return alert("Select a vendor");
                
                console.log("DEBUG: Step 2 - Selected Vendor:", selectedVendor);
                const requirements = await apiFetch('getvendorname', { vendor: selectedVendor });
                
                const container = document.getElementById('dynamicCredentials');
                container.innerHTML = `<h3>${selectedVendor.toUpperCase()} Credentials</h3>`;
                
                requirements.forEach(key => {
                    const label = (key === 'domain') ? "Datadog Site (e.g. us5.datadoghq.com)" : key.replace(/-/g, ' ');
                    container.innerHTML += `
                        <div class="form-group">
                            <label class="form-label">${label}</label> 
                            <input type="text" class="form-input vendor-input" id="${key}" placeholder="Enter ${key}">
                        </div>`;
                });
                currentStep = 3;
            }
            else if (currentStep === 3) {
                const payload = { vendor: selectedVendor };
                document.querySelectorAll('.vendor-input').forEach(input => {
                    payload[input.id] = input.value;
                });

                console.log("DEBUG: Step 3 - Sending Vendor Credentials:", payload);
                nextBtn.textContent = "Fetching...";
                
                window.fetchedData = await apiFetch('FetchMonitors', payload);
                renderDynamicTable(window.fetchedData);
                currentStep = 4;
            }
            else if (currentStep === 4) {
                const selected = getSelectedMonitorsForMigration();
                if (selected.length === 0) return alert("Please select at least one monitor.");
				console.log("DEBUG: Final Payload being sent to MigrateAction:", JSON.stringify(selected, null, 2));
                console.log("DEBUG: Step 4 - Migrating items:", selected);
                nextBtn.textContent = "Migrating...";
                
                await apiFetch('MigrateAction', selected);
                currentStep = 5;
            }
            updateStepDisplay();
        } catch (e) {
            console.error("DEBUG: Application Error:", e);
            alert("Error: " + e.message);
        } finally {
            nextBtn.textContent = (currentStep === 4) ? "Migrate" : "Next";
        }
    };

  
      function renderDynamicTable(data) {
	    const wrapper = document.getElementById('monitorsTableWrapper');
	    const monitors = data.monitors;
	    if (!monitors || monitors.length === 0) return;

	    
	    const globalFilters = document.getElementById('globalMetadataFilters');
	    if (globalFilters) {
	        globalFilters.style.setProperty('display', 'none', 'important');
	    }

	    const lookupKeys = Object.keys(data).filter(key => key !== 'monitors');

	    const hiddenFields = ['threshold_profile_id', 'user_group_ids', 'location_profile_id', 'notification_profile_id'];
	    const visibleKeys = Object.keys(monitors[0]).filter(k => !hiddenFields.includes(k.toLowerCase()));

	    let html = `<div style="overflow-x: auto; max-width: 100%; border: 1px solid #ddd; border-radius: 8px;">
	                <table class="monitors-table" style="min-width: 1200px;">
	                <thead><tr>
	                    <th><input type="checkbox" id="tableSelectAll" checked></th>`;
	    
	    visibleKeys.forEach(k => html += `<th>${k.replace(/_/g, ' ').toUpperCase()}</th>`);
	    
	    lookupKeys.forEach(lk => html += `<th>${lk.toUpperCase()}</th>`);
	    html += `</tr></thead><tbody>`;

	    monitors.forEach((m, idx) => {
	        html += `<tr data-index="${idx}">
	                    <td><input type="checkbox" checked class="mon-select"></td>`;
	        
	        visibleKeys.forEach(k => {
	            const val = m[k] || '';
	            const isType = k.toLowerCase() === 'type';
	            html += `<td><input type="text" value="${val}" class="dynamic-table-input ${isType ? 'type-cell' : ''}" data-field="${k}" ${isType ? 'readonly' : ''}></td>`;
	        });

	        lookupKeys.forEach(lk => {
	            const items = data[lk] || [];
	            let options = items.map(item => {
	                const id = item.profile_id || item.user_group_id || item.id;
	                const name = item.profile_name || item.display_name || item.name;
	                return `<option value="${id}">${name}</option>`;
	            }).join('');
	            
	            html += `<td><select class="row-lookup-cell form-input" data-lookup="${lk}">${options}</select></td>`;
	        });
	        html += `</tr>`;
	    });

	    wrapper.innerHTML = html + `</tbody></table></div>`;
	    
	    document.getElementById('tableSelectAll').onchange = (e) => {
	        document.querySelectorAll('.mon-select').forEach(cb => cb.checked = e.target.checked);
	    };
	}

	function getSelectedMonitorsForMigration() {
	    const rows = document.querySelectorAll('#monitorsTableWrapper tbody tr');
	    const selected = [];
	    
	    rows.forEach(row => {
	        if (row.querySelector('.mon-select').checked) {
	            const idx = row.dataset.index;
	            const monitor = JSON.parse(JSON.stringify(window.fetchedData.monitors[idx]));
	            
	            row.querySelectorAll('.dynamic-table-input').forEach(i => monitor[i.dataset.field] = i.value);
	            
	            row.querySelectorAll('.row-lookup-cell').forEach(select => {
	                const type = select.dataset.lookup; 
	                const val = select.value;
					console.log(`DEBUG: Row ${idx} - Type: ${type}, Selected ID: ${val}`);
	                if (type === 'locations') monitor.location_profile_id = val;
	                else if (type === 'notifications') monitor.notification_profile_id = val;
	                else if (type === 'groups') monitor.user_group_ids = [val];
	            });
	            selected.push(monitor);
	        }
	    });
	    return selected;
	}

    function updateStepDisplay() {
        document.querySelectorAll('.step-content').forEach(s => s.classList.remove('active'));
        const stepEl = document.getElementById(`step${currentStep}`);
        if (stepEl) stepEl.classList.add('active');

        if (currentStep === 4) modal.classList.add('fullscreen');
        else modal.classList.remove('fullscreen');
        
        document.querySelectorAll('.progress-step').forEach((step, idx) => {
            step.classList.toggle('active', idx + 1 === currentStep);
            step.classList.toggle('completed', idx + 1 < currentStep);
        });
        
        const progressPercent = ((currentStep - 1) / 4) * 100;
        const fill = document.getElementById('progressFill');
        if (fill) fill.style.width = progressPercent + '%';
        
        backBtn.style.display = (currentStep === 1 || currentStep === 5) ? 'none' : 'block';
    }

    backBtn.onclick = () => { 
        currentStep--; 
        updateStepDisplay(); 
    };
});