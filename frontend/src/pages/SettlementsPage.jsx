import React, { useEffect, useState } from 'react'
import { fetchSettlements } from '../api/api'

export default function SettlementsPage() {
  const [settlements, setSettlements] = useState([])
  useEffect(() => { fetchSettlements().then(setSettlements).catch(console.error) }, [])

  return (
    <div className="card">
      <h3>Settlements</h3>
      <table className="table">
        <thead>
          <tr><th>Payer</th><th>Recepient</th><th>Amount</th></tr>
        </thead>
        <tbody>
          {settlements && settlements.length ? settlements.map((s,i)=> (
            <tr key={i}><td>{s.payer || s.from}</td><td>{s.recipient || s.to}</td><td>{s.amount}</td></tr>
          )) : <tr><td colSpan="3">No settlements</td></tr>}
        </tbody>
      </table>
    </div>
  )
}
