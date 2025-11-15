import React, { useEffect, useState } from 'react'
import { fetchFriends, addFriend, deleteFriend, resetAll } from '../api/api'

export default function FriendsPage() {
  const [friends, setFriends] = useState([])
  const [form, setForm] = useState({ name: '', email: '', phone: '' })
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    load()
  }, [])

  async function load() {
    setLoading(true)
    try {
      const data = await fetchFriends()
      setFriends(data)
    } catch (err) {
      console.error(err)
      alert('Failed to load friends')
    } finally {
      setLoading(false)
    }
  }

  async function handleSubmit(e) {
    e.preventDefault()
    try {
      await addFriend(form)
      setForm({ name: '', email: '', phone: '' })
      load()
    } catch (err) {
      console.error(err)
      alert('Failed to add friend')
    }
  }

  return (
    <div className="grid">
      <div className="card">
        <h3>Add Friend</h3>
        <form onSubmit={handleSubmit} className="form">
          <label>
            Name
            <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required />
          </label>
          <label>
            Email
            <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} />
          </label>
          <label>
            Phone
            <input value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} />
          </label>
          <button type="submit">Add</button>
        </form>
      </div>

      <div className="card">
        <div style={{display:'flex',justifyContent:'space-between',alignItems:'center'}}>
          <h3 style={{margin:0}}>Friends</h3>
          <button type="button" onClick={async()=>{ if(confirm('Reset ALL data? This cannot be undone.')) { await resetAll(); load(); } }} style={{background:'#ef4444'}}>Reset Data</button>
        </div>
        {loading ? <p>Loading...</p> : (
          <ul className="list" style={{marginTop:10}}>
            {friends && friends.length ? friends.map((f) => (
              <li key={f.userId} style={{display:'flex',justifyContent:'space-between',alignItems:'center',padding:'4px 0'}}>
                <span>{f.fullName || f.name || f.username || f.email}</span>
                <button type="button" onClick={async()=>{ await deleteFriend(f.userId); load(); }} style={{background:'#dc2626'}}>Delete</button>
              </li>
            )) : <li>No friends found</li>}
          </ul>
        )}
      </div>
    </div>
  )
}
