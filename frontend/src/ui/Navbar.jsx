import React from 'react'
import { NavLink } from 'react-router-dom'

export default function Navbar() {
  return (
    <header className="navbar">
      <div className="brand">Smart Expense</div>
      <nav>
        <NavLink to="/" className="navlink">Home</NavLink>
        <NavLink to="/friends" className="navlink">Friends</NavLink>
        <NavLink to="/expenses" className="navlink">Expenses</NavLink>
        <NavLink to="/expenses/new" className="navlink">Add Expense</NavLink>
        <NavLink to="/balances" className="navlink">Balances</NavLink>
        <NavLink to="/settlements" className="navlink">Settlements</NavLink>
      </nav>
    </header>
  )
}
