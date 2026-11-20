import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { UserPlus } from 'lucide-react'
import { api } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { AuthForm } from '../components/AuthForm'
import { FormField } from '../components/FormField'
import { Button } from '../components/Button'

export function RegisterPage() {
  const [form, setForm] = useState({ name: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const submit = async (event) => {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      const user = await api.register(form)
      login(user)
      navigate('/')
    } catch (exception) {
      setError(exception.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthForm title="Crie sua conta" subtitle="Cadastre-se para participar das enquetes." onSubmit={submit}>
      <FormField label="Nome" id="name" required value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} placeholder="Seu nome" />
      <FormField label="E-mail" id="email" type="email" required value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} placeholder="voce@email.com" />
      <FormField label="Senha" id="password" type="password" required minLength={6} value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} placeholder="Mínimo 6 caracteres" />
      {error && <p className="rounded-xl border border-red-200 bg-red-50 px-3 py-2.5 text-sm text-red-700">{error}</p>}
      <Button type="submit" className="w-full" disabled={loading}>
        <UserPlus size={17} /> {loading ? 'Criando conta...' : 'Criar conta'}
      </Button>
      <p className="text-center text-sm text-zinc-500">
        Já possui conta? <Link to="/login" className="font-semibold text-violet-600 hover:text-violet-700">Entrar</Link>
      </p>
    </AuthForm>
  )
}
