/* ============ Model layer (mirrors the Java classes 1:1) ============ */

class EventBase {
  constructor(name, date, timeSlot, venue, category, maxParticipants) {
    this.id = crypto.randomUUID();
    this.name = name;
    this.date = date;
    this.timeSlot = timeSlot;
    this.venue = venue;
    this.category = category;
    this.maxParticipants = maxParticipants;
    this.registrations = [];
  }
  confirmedCount() {
    return this.registrations.filter(r => r.status === 'CONFIRMED').length;
  }
  isFull() { return this.confirmedCount() >= this.maxParticipants; }
  calculateScore() { return 0; }
}

class TechnicalEvent extends EventBase {
  constructor(name, date, timeSlot, venue, maxParticipants, subType) {
    super(name, date, timeSlot, venue, 'TECHNICAL', maxParticipants);
    this.subType = subType;
  }
  // rawInputs: QUIZ [correct,total] | CODING_CONTEST [solved,penalty] | HACKATHON [judge,bonus]
  calculateScore(...rawInputs) {
    const [a, b] = rawInputs;
    switch (this.subType) {
      case 'QUIZ': return b ? (a / b) * 100 : 0;
      case 'CODING_CONTEST': return Math.max(0, a * 100 - (b || 0) * 2);
      case 'HACKATHON': return Math.min(100, a * 10 + (b || 0));
      default: return 0;
    }
  }
}

class CulturalEvent extends EventBase {
  constructor(name, date, timeSlot, venue, maxParticipants, subType) {
    super(name, date, timeSlot, venue, 'CULTURAL', maxParticipants);
    this.subType = subType;
  }
  // rawInputs: up to 3 judge scores (0-10) -> averaged, scaled to 100
  calculateScore(...rawInputs) {
    if (!rawInputs.length) return 0;
    const avg = rawInputs.reduce((s, v) => s + v, 0) / rawInputs.length;
    return avg * 10;
  }
}

class Participant {
  constructor(name, email, rollNumber, department) {
    this.id = crypto.randomUUID();
    this.name = name;
    this.email = email;
    this.rollNumber = rollNumber;
    this.department = department;
    this.registrations = [];
  }
  hasClashWith(candidate) {
    return this.registrations.some(r =>
      r.status === 'CONFIRMED' &&
      r.event !== candidate &&
      r.event.date === candidate.date &&
      r.event.timeSlot === candidate.timeSlot
    );
  }
}

class Registration {
  constructor(participant, event, status) {
    this.id = crypto.randomUUID();
    this.participant = participant;
    this.event = event;
    this.status = status;
    this.registeredAt = new Date();
    this.score = null;
    this.winner = false;
  }
}

class EventManager {
  constructor() { this.events = []; this.participants = []; }

  register(participant, event) {
    if (participant.hasClashWith(event)) {
      throw new Error(`Clash: ${participant.name} already has a confirmed event in ${event.date} ${event.timeSlot}`);
    }
    const status = event.isFull() ? 'WAITLISTED' : 'CONFIRMED';
    const reg = new Registration(participant, event, status);
    event.registrations.push(reg);
    participant.registrations.push(reg);
    return reg;
  }
}

/* ============ App state & wiring ============ */

const manager = new EventManager();
const el = id => document.getElementById(id);

const SUBTYPES = {
  TECHNICAL: [
    ['QUIZ', 'Quiz'],
    ['CODING_CONTEST', 'Coding contest'],
    ['HACKATHON', 'Hackathon'],
  ],
  CULTURAL: [
    ['DANCE', 'Dance'],
    ['MUSIC', 'Music'],
    ['DRAMA', 'Drama'],
    ['FASHION_SHOW', 'Fashion show'],
  ],
};

function refreshSubtypeOptions() {
  const cat = el('ev-category').value;
  el('ev-subtype').innerHTML = SUBTYPES[cat].map(([v, l]) => `<option value="${v}">${l}</option>`).join('');
}
el('ev-category').addEventListener('change', refreshSubtypeOptions);
refreshSubtypeOptions();

el('add-event-btn').addEventListener('click', () => {
  const name = el('ev-name').value.trim();
  const date = el('ev-date').value;
  const slot = el('ev-slot').value.trim();
  const venue = el('ev-venue').value.trim();
  const cap = parseInt(el('ev-cap').value, 10);
  const cat = el('ev-category').value;
  const subType = el('ev-subtype').value;
  if (!name || !date || !slot || !venue || !cap) return;

  const ev = cat === 'TECHNICAL'
    ? new TechnicalEvent(name, date, slot, venue, cap, subType)
    : new CulturalEvent(name, date, slot, venue, cap, subType);
  manager.events.push(ev);
  el('event-form').reset();
  refreshSubtypeOptions();
  renderAll();
});

el('add-participant-btn').addEventListener('click', () => {
  const name = el('p-name').value.trim();
  const roll = el('p-roll').value.trim();
  const dept = el('p-dept').value.trim();
  const email = el('p-email').value.trim();
  if (!name || !roll || !dept || !email) return;
  manager.participants.push(new Participant(name, email, roll, dept));
  el('participant-form').reset();
  renderAll();
});

el('register-btn').addEventListener('click', () => {
  const p = manager.participants.find(x => x.id === el('reg-participant').value);
  const e = manager.events.find(x => x.id === el('reg-event').value);
  const log = el('reg-log');
  if (!p || !e) {
    logLine('Add a participant and an event first.', true);
    return;
  }
  try {
    const reg = manager.register(p, e);
    logLine(`${p.name} -> ${e.name}: ${reg.status}${reg.status === 'WAITLISTED' ? ' (event at capacity)' : ''}`);
  } catch (err) {
    logLine(err.message, true);
  }
  renderAll();
});

function logLine(text, isErr) {
  const log = el('reg-log');
  const div = document.createElement('div');
  div.className = 'log-line' + (isErr ? ' err' : '');
  div.textContent = (isErr ? '✕ ' : '✓ ') + text;
  log.prepend(div);
}

el('export-json-btn').addEventListener('click', () => {
  const data = manager.events.map(e => ({
    event: e.name, category: e.category, date: e.date, timeSlot: e.timeSlot, venue: e.venue,
    registrations: e.registrations.map(r => ({
      participant: r.participant.name, roll: r.participant.rollNumber,
      status: r.status, score: r.score, winner: r.winner
    }))
  }));
  downloadFile('results.json', JSON.stringify(data, null, 2), 'application/json');
});

el('export-csv-btn').addEventListener('click', () => {
  const rows = [['event', 'participant', 'roll', 'status', 'score', 'winner']];
  manager.events.forEach(e => e.registrations.forEach(r => {
    rows.push([e.name, r.participant.name, r.participant.rollNumber, r.status, r.score ?? '', r.winner]);
  }));
  const csv = rows.map(r => r.map(v => `"${String(v).replace(/"/g, '""')}"`).join(',')).join('\n');
  downloadFile('registrations.csv', csv, 'text/csv');
});

function downloadFile(filename, content, type) {
  const blob = new Blob([content], { type });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url; a.download = filename;
  document.body.appendChild(a); a.click(); a.remove();
  URL.revokeObjectURL(url);
}

function generateCertificateText(reg) {
  const p = reg.participant, e = reg.event;
  const type = reg.winner ? 'WINNER' : 'PARTICIPATION';
  const lines = [
    '========================================',
    `      CERTIFICATE OF ${type}`,
    '========================================',
    '',
    'This certifies that',
    '',
    `    ${p.name} (${p.rollNumber})`,
    `    ${p.department}`,
    '',
    `participated in "${e.name}"`,
    `held on ${e.date} at ${e.venue}.`,
    ''
  ];
  if (reg.score !== null) lines.push(`Score: ${reg.score.toFixed(1)}/100`, '');
  lines.push(`Issued: ${reg.registeredAt.toDateString()}`);
  return lines.join('\n');
}

/* ============ Rendering ============ */

function renderAll() {
  renderEvents();
  renderParticipants();
  renderRegisterOptions();
  renderResults();
}

function renderEvents() {
  el('event-count-sub').textContent = `${manager.events.length} event${manager.events.length === 1 ? '' : 's'}`;
  const list = el('event-list');
  if (!manager.events.length) {
    list.innerHTML = '<div class="empty">No events yet — add one above.</div>';
    return;
  }
  list.innerHTML = manager.events.map(e => {
    const pct = Math.min(100, Math.round((e.confirmedCount() / e.maxParticipants) * 100));
    return `<div class="stub ${e.isFull() ? 'full' : ''}">
      <span class="badge ${e.category.toLowerCase()}">${e.category === 'TECHNICAL' ? 'Technical' : 'Cultural'} · ${e.subType.replace('_',' ').toLowerCase()}</span>
      <h3>${escapeHtml(e.name)}</h3>
      <div class="meta">${e.date || 'no date'} · ${escapeHtml(e.timeSlot)}</div>
      <div class="meta">${escapeHtml(e.venue)}</div>
      <div class="cap">
        ${e.confirmedCount()}/${e.maxParticipants} confirmed
        <div class="bar"><div style="width:${pct}%"></div></div>
      </div>
    </div>`;
  }).join('');
}

function renderParticipants() {
  el('participant-count-sub').textContent = `${manager.participants.length} participant${manager.participants.length === 1 ? '' : 's'}`;
  const list = el('participant-list');
  if (!manager.participants.length) {
    list.innerHTML = '<div class="empty">No participants yet — add one above.</div>';
    return;
  }
  list.innerHTML = manager.participants.map(p => `
    <div class="row">
      <span class="name">${escapeHtml(p.name)}</span>
      <span class="roll">${escapeHtml(p.rollNumber)}</span>
      <span>${escapeHtml(p.department)}</span>
      <span class="spacer"></span>
      <span class="mono" style="font-size:12px;color:var(--ink-soft)">${p.registrations.length} registration${p.registrations.length === 1 ? '' : 's'}</span>
    </div>`).join('');
}

function renderRegisterOptions() {
  el('reg-participant').innerHTML = manager.participants.length
    ? manager.participants.map(p => `<option value="${p.id}">${escapeHtml(p.name)} (${escapeHtml(p.rollNumber)})</option>`).join('')
    : '<option value="">Add a participant first</option>';
  el('reg-event').innerHTML = manager.events.length
    ? manager.events.map(e => `<option value="${e.id}">${escapeHtml(e.name)} — ${e.date || 'no date'} ${escapeHtml(e.timeSlot)}</option>`).join('')
    : '<option value="">Add an event first</option>';
}

function renderResults() {
  const container = el('results-list');
  const confirmed = manager.events.flatMap(e => e.registrations.filter(r => r.status === 'CONFIRMED').map(r => ({ r, e })));
  if (!confirmed.length) {
    container.innerHTML = '<div class="empty">No confirmed registrations yet.</div>';
    return;
  }
  container.innerHTML = confirmed.map(({ r, e }) => `
    <div class="result-row" data-reg="${r.id}">
      <span class="name">${escapeHtml(r.participant.name)}</span>
      <span class="mono" style="font-size:12px;color:var(--ink-soft)">${escapeHtml(e.name)}</span>
      <span class="spacer"></span>
      ${r.winner ? '<span class="winner-tag">winner</span>' : ''}
      <input type="number" min="0" max="100" step="0.1" placeholder="score" value="${r.score ?? ''}" data-action="score" data-reg="${r.id}">
      <label class="win"><input type="checkbox" data-action="winner" data-reg="${r.id}" ${r.winner ? 'checked' : ''}> Winner</label>
      <button class="btn small secondary" data-action="cert" data-reg="${r.id}">Certificate</button>
    </div>`).join('');
}

document.getElementById('results-list').addEventListener('change', (evt) => {
  const t = evt.target;
  const regId = t.dataset.reg;
  if (!regId) return;
  const reg = findRegistration(regId);
  if (!reg) return;
  if (t.dataset.action === 'score') {
    reg.score = t.value === '' ? null : parseFloat(t.value);
  } else if (t.dataset.action === 'winner') {
    reg.winner = t.checked;
    renderResults();
  }
});

document.getElementById('results-list').addEventListener('click', (evt) => {
  const t = evt.target;
  if (t.dataset.action === 'cert') {
    const reg = findRegistration(t.dataset.reg);
    if (!reg) return;
    downloadFile(`certificate-${reg.participant.rollNumber}-${reg.event.name.replace(/\s+/g,'_')}.txt`, generateCertificateText(reg), 'text/plain');
  }
});

function findRegistration(id) {
  for (const e of manager.events) {
    const r = e.registrations.find(x => x.id === id);
    if (r) return r;
  }
  return null;
}

function escapeHtml(str) {
  return String(str).replace(/[&<>"']/g, c => ({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;' }[c]));
}

/* ============ Seed demo data so the board isn't empty on first load ============ */
(function seed() {
  const quiz = new TechnicalEvent('Byte Battle', new Date().toISOString().slice(0,10), '10:00-12:00', 'Seminar Hall A', 2, 'QUIZ');
  const dance = new CulturalEvent('Rhythm Rush', new Date().toISOString().slice(0,10), '10:00-12:00', 'Open Air Theatre', 2, 'DANCE');
  manager.events.push(quiz, dance);
  const a = new Participant('Arjun Menon', 'arjun@college.edu', 'CS21B045', 'CSE');
  const b = new Participant('Sara Thomas', 'sara@college.edu', 'EC21B012', 'ECE');
  manager.participants.push(a, b);
  manager.register(a, quiz);
  renderAll();
})();
