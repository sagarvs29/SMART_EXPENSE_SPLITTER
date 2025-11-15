import React, { useEffect, useState } from 'react'
import { fetchBalances } from '../api/api'

export default function BalancesPage() {
  const [balances, setBalances] = useState([])
  useEffect(() => { fetchBalances().then(setBalances).catch(console.error) }, [])

  return (
    <div className="card">
      <h3>Balances</h3>
      <table className="table">
        <thead>
          <tr><th>Person</th><th>Net Balance</th></tr>
        </thead>
        <tbody>
          {balances && balances.length ? balances.map((b,i)=> (
            <tr key={i}><td>{b.person || b.name || b.username}</td><td>{b.amount}</td></tr>
          )) : <tr><td colSpan="2">No balances</td></tr>}
        </tbody>
      </table>
    </div>
  )
}
