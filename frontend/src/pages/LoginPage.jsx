import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { LogIn } from 'lucide-react'
import { api } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { AuthForm } from '../components/AuthForm'
import { FormField } from '../components/FormField'
import { Button } from '../components/Button'

export function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const submit = async (event) => {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      const user = await api.login(form)
      login(user)
      navigate('/')
    } catch (exception) {
      setError(exception.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthForm title="Bem-vindo de volta" subtitle="Entre para votar e criar novas enquetes." onSubmit={submit}>
      <FormField label="E-mail" id="email" type="email" required value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} placeholder="voce@email.com" />
      <FormField label="Senha" id="password" type="password" required value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} placeholder="Sua senha" />
      {error && <p className="rounded-xl border border-red-200 bg-red-50 px-3 py-2.5 text-sm text-red-700">{error}</p>}
      <Button type="submit" className="w-full" disabled={loading}>
        <LogIn size={17} /> {loading ? 'Entrando...' : 'Entrar'}
      </Button>
      <p className="text-center text-sm text-zinc-500">
        Ainda não tem conta? <Link to="/register" className="font-semibold text-violet-600 hover:text-violet-700">Criar conta</Link>
      </p>
    </AuthForm>
  )
}
