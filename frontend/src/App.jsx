import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import Navbar from './ui/Navbar'
import HomePage from './pages/HomePage'
import FriendsPage from './pages/FriendsPage'
import AddExpensePage from './pages/AddExpensePage'
import ViewExpensesPage from './pages/ViewExpensesPage'
import BalancesPage from './pages/BalancesPage'
import SettlementsPage from './pages/SettlementsPage'

export default function App() {
  return (
    <div className="app-root">
      <Navbar />
      <main className="container">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/friends" element={<FriendsPage />} />
          <Route path="/expenses/new" element={<AddExpensePage />} />
          <Route path="/expenses" element={<ViewExpensesPage />} />
          <Route path="/balances" element={<BalancesPage />} />
          <Route path="/settlements" element={<SettlementsPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  )
}
