import React, { useEffect, useState } from 'react'
import { fetchFriends, addExpense } from '../api/api'

export default function AddExpensePage() {
  const [friends, setFriends] = useState([])
  const [form, setForm] = useState({ amount: '', description: '', payerId: '', date: '', participantIds: [] })

  useEffect(() => { fetchFriends().then(setFriends).catch(console.error) }, [])

  async function submit(e) {
    e.preventDefault()
    try {
      const payload = {
        amount: parseFloat(form.amount),
        description: form.description,
        payerId: form.payerId ? parseInt(form.payerId, 10) : undefined,
        date: form.date || new Date().toISOString().slice(0,10),
        participantIds: (form.participantIds || []).map(x => parseInt(x,10)),
        splitType: 'equal'
      }
      await addExpense(payload)
      alert('Expense added')
      setForm({ amount: '', description: '', payerId: '', date: '' })
    } catch (err) {
      console.error(err)
      alert('Failed to add expense')
    }
  }

  return (
    <div className="card">
      <h3>Add Expense</h3>
      <form onSubmit={submit} className="form">
        <label>
          Amount
          <input type="number" step="0.01" value={form.amount} onChange={e => setForm({...form, amount: e.target.value})} required />
        </label>
        <label>
          Description
          <input value={form.description} onChange={e => setForm({...form, description: e.target.value})} />
        </label>
        <label>
          Payer
          <select value={form.payerId} onChange={e => setForm({...form, payerId: e.target.value})} required>
            <option value="">-- choose payer --</option>
            {friends.map((f) => (
              <option key={f.userId ?? f.username ?? f.email} value={f.userId}>
                {f.fullName || f.name || f.username || f.email}
              </option>
            ))}
          </select>
        </label>
        <label>
          Participants (split equally)
          <select multiple value={form.participantIds} onChange={e => {
            const opts = Array.from(e.target.selectedOptions).map(o => o.value)
            setForm({...form, participantIds: opts})
          }} size={Math.min(6, friends.length || 6)}>
            {friends.map((f) => (
              <option key={f.userId} value={f.userId}>{f.fullName || f.username || f.email}</option>
            ))}
          </select>
          <small>Select all members who shared this expense (including the payer if they participated)</small>
        </label>
        <label>
          Date
          <input type="date" value={form.date} onChange={e => setForm({...form, date: e.target.value})} />
        </label>
        <button type="submit">Create</button>
      </form>
    </div>
  )
}
