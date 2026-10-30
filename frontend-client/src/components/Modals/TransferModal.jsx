import React, { useState } from 'react';
import { X, ArrowLeftRight, AlertTriangle } from 'lucide-react';

export default function TransferModal({ isOpen, onClose, accounts, onTransfer }) {
  const [sourceId, setSourceId] = useState(accounts[0]?.id || '');
  const [targetId, setTargetId] = useState(accounts[1]?.id || '');
  const [amount, setAmount] = useState('200.00');
  const [description, setDescription] = useState('Virement bancaire interne');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  if (!isOpen) return null;

  const sourceAccount = accounts.find(a => a.id === sourceId) || accounts[0];
  const targetAccount = accounts.find(a => a.id === targetId) || accounts[1];

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg('');

    if (sourceId === targetId) {
      setErrorMsg('Le compte source et le compte destinataire doivent être distincts');
      return;
    }

    const numAmount = parseFloat(amount);
    if (numAmount <= 0) {
      setErrorMsg('Le montant du virement doit être strictement positif');
      return;
    }

    if (sourceAccount && sourceAccount.balance < numAmount) {
      setErrorMsg(`Solde insuffisant sur le compte source (${sourceAccount.balance} ${sourceAccount.currency})`);
      return;
    }

    if (sourceAccount?.status !== 'ACTIVE' || targetAccount?.status !== 'ACTIVE') {
      setErrorMsg('Les deux comptes doivent être actifs pour effectuer un virement');
      return;
    }

    setIsSubmitting(true);
    try {
      await onTransfer({
        sourceAccountId: sourceId,
        targetAccountId: targetId,
        amount: numAmount,
        currency: sourceAccount?.currency || 'MAD',
        description
      });
      setIsSubmitting(false);
      onClose();
    } catch (err) {
      setIsSubmitting(false);
      setErrorMsg(err.message || 'Erreur lors du virement');
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm p-4 animate-fade-in">
      <div className="bg-white rounded-2xl border border-slate-200 shadow-2xl w-full max-w-lg overflow-hidden animate-modal-in">
        {/* Header */}
        <div className="px-6 py-4 border-b border-slate-200 flex items-center justify-between bg-slate-50/80">
          <div className="flex items-center gap-2">
            <ArrowLeftRight className="w-4 h-4 text-teal-600" />
            <h3 className="font-semibold text-slate-900 text-sm">Effectuer un Virement de Compte à Compte</h3>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition active:scale-95"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Error Alert */}
        {errorMsg && (
          <div className="mx-6 mt-4 p-3 rounded-lg bg-rose-50 border border-rose-200 flex items-center gap-2 text-xs text-rose-700">
            <AlertTriangle className="w-4 h-4 flex-shrink-0" />
            <span>{errorMsg}</span>
          </div>
        )}

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Compte Source (Débit)
            </label>
            <select
              value={sourceId}
              onChange={(e) => setSourceId(e.target.value)}
              className="w-full text-xs px-3.5 py-2.5 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-600 transition"
            >
              {accounts.map((acc) => (
                <option key={acc.id} value={acc.id}>
                  {acc.customerReference} — Solde : {acc.balance} {acc.currency}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Compte Destinataire (Crédit)
            </label>
            <select
              value={targetId}
              onChange={(e) => setTargetId(e.target.value)}
              className="w-full text-xs px-3.5 py-2.5 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-600 transition"
            >
              {accounts.map((acc) => (
                <option key={acc.id} value={acc.id}>
                  {acc.customerReference} — Solde : {acc.balance} {acc.currency}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Montant ({sourceAccount?.currency || 'MAD'})
            </label>
            <input
              type="number"
              step="0.01"
              min="0.01"
              required
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className="w-full text-xs font-mono px-3.5 py-2.5 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-600 transition"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Motif du Virement
            </label>
            <input
              type="text"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full text-xs px-3.5 py-2.5 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-600 transition"
            />
          </div>

          {/* Footer Actions */}
          <div className="pt-3 border-t border-slate-200 flex items-center justify-end gap-2.5">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 rounded-lg text-slate-600 hover:bg-slate-100 text-xs font-medium transition"
            >
              Annuler
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-4 py-2 rounded-lg bg-teal-600 hover:bg-teal-700 active:scale-95 text-white text-xs font-medium transition shadow-sm disabled:opacity-50"
            >
              {isSubmitting ? 'Transfert...' : 'Exécuter le Virement'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

