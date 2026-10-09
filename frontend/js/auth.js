const login = document.querySelector('#loginForm');
function setBusy(form, busy) { const button = form.querySelector('button[type="submit"]'); if (button) { button.disabled = busy; button.dataset.label ??= button.textContent; button.textContent = busy ? 'Working...' : button.dataset.label; } }
function showMessage(el, message, success = false) { el.textContent = message; el.className = 'alert' + (success ? ' success' : ''); el.style.display = 'block'; }
if (login) login.addEventListener('submit', async event => {
  event.preventDefault(); const message = login.parentElement.querySelector('.alert'); message.style.display = 'none';
  if (!login.reportValidity()) return; setBusy(login, true);
  try {
    const data = await api('/auth/login', { method: 'POST', body: JSON.stringify({ email: login.email.value.trim().toLowerCase(), password: login.password.value }) });
    const expected = document.body.dataset.expectedRole;
    if (expected === 'FACULTY' && !['FACULTY', 'ADMIN'].includes(data.user.role)) throw Error('This account is not authorized for faculty access.');
    if (expected && expected !== 'FACULTY' && data.user.role !== expected) throw Error('This account belongs to the ' + data.user.role.toLowerCase() + ' login.');
    const session = { token: data.token, user: data.user }; localStorage.removeItem('trackerSession'); sessionStorage.removeItem('trackerSession');
    (login.remember?.checked ? localStorage : sessionStorage).setItem('trackerSession', JSON.stringify(session));
    showMessage(message, 'Login successful. Choose your workspace...', true); setTimeout(() => go('role-selection.html'), 250);
  } catch (error) { showMessage(message, error.message); } finally { setBusy(login, false); }
});
const register = document.querySelector('#registerForm');
if (register) register.addEventListener('submit', async event => {
  event.preventDefault(); const message = register.parentElement.querySelector('.alert'); message.style.display = 'none';
  if (!register.reportValidity()) return;
  if (!register.role.value) return showMessage(message, 'Please select Student or Faculty.');
  if (register.password.value !== register.confirmPassword.value) { showMessage(message, 'Passwords do not match.'); register.confirmPassword.focus(); return; }
  if (!/[A-Z]/.test(register.password.value) || !/[a-z]/.test(register.password.value) || !/[0-9]/.test(register.password.value)) return showMessage(message, 'Password must include uppercase, lowercase, and a number.');
  if (register.role.value === 'FACULTY' && !register.facultyCode.value.trim()) return showMessage(message, 'Please enter your faculty access code.');
  setBusy(register, true);
  try { await api('/auth/register', { method: 'POST', body: JSON.stringify({ name: register.name.value.trim(), email: register.email.value.trim().toLowerCase(), phone: register.phone.value.trim(), password: register.password.value, confirmPassword: register.confirmPassword.value, role: register.role.value, facultyCode: register.facultyCode.value.trim() }) }); showMessage(message, 'Account created successfully. Redirecting to sign in...', true); setTimeout(() => go('index.html'), 800); }
  catch (error) { showMessage(message, error.message); } finally { setBusy(register, false); }
});
const forgot = document.querySelector('#forgotForm');
if (forgot) forgot.addEventListener('submit', async event => { event.preventDefault(); const message = forgot.parentElement.querySelector('.alert'); try { const data = await api('/auth/forgot-password', { method: 'POST', body: JSON.stringify({ email: forgot.email.value.trim() }) }); showMessage(message, data.message, true); } catch (error) { showMessage(message, error.message); } });
function togglePassword(id, button) { const input = document.getElementById(id); input.type = input.type === 'password' ? 'text' : 'password'; button.textContent = input.type === 'password' ? 'Show' : 'Hide'; }
