import React, { useEffect, useState } from 'react'
import { fetchExpenses, fetchFriends } from '../api/api'

export default function ViewExpensesPage() {
  const [expenses, setExpenses] = useState([])
  const [friends, setFriends] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => { load() }, [])

  async function load() {
    setLoading(true)
    try {
      const [e, f] = await Promise.all([fetchExpenses(), fetchFriends()])
      setExpenses(e)
      setFriends(f)
    } catch (err) {
      console.error(err)
      alert('Failed to load data')
    } finally {
      setLoading(false)
    }
  }

  function payerName(payerId) {
    if (payerId === undefined || payerId === null) return '—'
    const p = friends.find(x => x.userId === payerId)
    return p ? (p.fullName || p.name || p.username || p.email) : String(payerId)
  }

  return (
    <div className="card">
      <h3>Expenses</h3>
      {loading ? <p>Loading...</p> : (
        <table className="table">
          <thead>
            <tr><th>Date</th><th>Description</th><th>Amount</th><th>Payer</th></tr>
          </thead>
          <tbody>
            {expenses && expenses.length ? expenses.map((ex, i) => (
              <tr key={i}>
                <td>{ex.date || '—'}</td>
                <td>{ex.description || '—'}</td>
                <td>{ex.amount || '—'}</td>
                <td>{payerName(ex.payerId)}</td>
              </tr>
            )) : (
              <tr><td colSpan="4">No expenses</td></tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  )
}
