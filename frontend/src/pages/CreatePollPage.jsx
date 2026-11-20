import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { Plus, Trash2 } from 'lucide-react'
import { api } from '../services/api'
import { useAuth } from '../context/AuthContext'
import { Button } from '../components/Button'
import { FormField } from '../components/FormField'

export function CreatePollPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ title: '', question: '', finishDate: '', options: ['', ''] })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  if (!user) return <Navigate to="/login" replace />

  const setOption = (index, value) => {
    setForm((current) => ({
      ...current,
      options: current.options.map((option, optionIndex) => optionIndex === index ? value : option),
    }))
  }

  const submit = async (event) => {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      const finishDate = form.finishDate.includes('T')
        ? form.finishDate
        : `${form.finishDate}T23:59:00`
      await api.createPoll(user.id, {
        title: form.title,
        question: form.question,
        finishDate,
        options: form.options.filter((option) => option.trim()),
      })
      navigate('/')
    } catch (exception) {
      setError(exception.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="min-h-screen px-4 pb-16 pt-28 sm:px-6">
      <div className="mx-auto max-w-2xl rounded-3xl border border-violet-100 bg-white p-6 shadow-[0_18px_50px_-24px_rgba(124,58,237,0.28)] sm:p-9">
        <p className="text-sm font-semibold text-violet-600">Nova enquete</p>
        <h1 className="mt-1 text-3xl font-bold tracking-tight text-zinc-950">Pergunte à comunidade</h1>

        <form className="mt-8 space-y-5" onSubmit={submit}>
          <FormField label="Título" id="title" required value={form.title} onChange={(event) => setForm({ ...form, title: event.target.value })} placeholder="Ex: Qual tema da próxima reunião?" />
          <FormField label="Pergunta" id="question" required value={form.question} onChange={(event) => setForm({ ...form, question: event.target.value })} placeholder="Descreva a decisão que precisa ser tomada" />
          <FormField label="Data de encerramento" id="finishDate" type="datetime-local" required value={form.finishDate} onChange={(event) => setForm({ ...form, finishDate: event.target.value })} />

          <div>
            <div className="mb-2 flex items-center justify-between">
              <label className="text-sm font-semibold text-zinc-800">Opções</label>
              <button type="button" onClick={() => setForm({ ...form, options: [...form.options, ''] })} className="flex items-center gap-1 text-xs font-semibold text-violet-600">
                <Plus size={14} /> Adicionar
              </button>
            </div>
            <div className="space-y-2">
              {form.options.map((option, index) => (
                <div key={index} className="flex gap-2">
                  <input
                    required
                    value={option}
                    onChange={(event) => setOption(index, event.target.value)}
                    placeholder={`Opção ${index + 1}`}
                    className="min-w-0 flex-1 rounded-xl border border-zinc-200 px-3.5 py-3 text-sm outline-none focus:border-violet-400 focus:ring-4 focus:ring-violet-100"
                  />
                  {form.options.length > 2 && (
                    <button type="button" onClick={() => setForm({ ...form, options: form.options.filter((_, itemIndex) => itemIndex !== index) })} className="grid size-12 shrink-0 place-items-center rounded-xl border border-zinc-200 text-zinc-400 hover:border-red-200 hover:text-red-600" aria-label="Remover opção">
                      <Trash2 size={16} />
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>

          {error && <p className="rounded-xl border border-red-200 bg-red-50 px-3 py-2.5 text-sm text-red-700">{error}</p>}
          <Button type="submit" className="w-full" disabled={loading}>{loading ? 'Criando...' : 'Criar enquete'}</Button>
        </form>
      </div>
    </main>
  )
}
