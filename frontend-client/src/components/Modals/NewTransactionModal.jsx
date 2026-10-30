import React, { useState } from 'react';
import { X, CreditCard, AlertTriangle } from 'lucide-react';

export default function NewTransactionModal({ isOpen, onClose, accounts, onCreate }) {
  const [selectedAccountId, setSelectedAccountId] = useState(accounts[0]?.id || '');
  const [amount, setAmount] = useState('150.00');
  const [merchantCategory, setMerchantCategory] = useState('TECH');
  const [country, setCountry] = useState('MA');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  if (!isOpen) return null;

  const currentAccount = accounts.find(a => a.id === selectedAccountId) || accounts[0];

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMsg('');

    const numAmount = parseFloat(amount);
    if (numAmount <= 0) {
      setErrorMsg('Le montant doit être strictement positif');
      return;
    }

    if (numAmount > 1000) {
      setErrorMsg('Plafond dépassé : le montant maximum autorisé par transaction est de 1 000 (Config Server)');
      return;
    }

    if (currentAccount && currentAccount.status !== 'ACTIVE') {
      setErrorMsg('Ce compte est bloqué. Impossible de réaliser des transactions.');
      return;
    }

    if (currentAccount && currentAccount.balance < numAmount) {
      setErrorMsg(`Solde insuffisant (${currentAccount.balance} ${currentAccount.currency})`);
      return;
    }

    setIsSubmitting(true);
    try {
      await onCreate({
        accountId: selectedAccountId || currentAccount?.id,
        amount: numAmount,
        currency: currentAccount?.currency || 'MAD',
        merchantCategory,
        country: country.toUpperCase()
      });
      setIsSubmitting(false);
      onClose();
    } catch (err) {
      setIsSubmitting(false);
      setErrorMsg(err.message || 'Erreur lors du traitement de la transaction');
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm p-4 animate-fade-in">
      <div className="bg-white rounded-2xl border border-slate-200 shadow-2xl w-full max-w-lg overflow-hidden animate-modal-in">
        {/* Header */}
        <div className="px-6 py-4 border-b border-slate-200 flex items-center justify-between bg-slate-50/80">
          <div className="flex items-center gap-2">
            <CreditCard className="w-4 h-4 text-teal-600" />
            <h3 className="font-semibold text-slate-900 text-sm">Enregistrement d'une Transaction</h3>
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
              Sélectionner le Compte Bancaire
            </label>
            <select
              value={selectedAccountId || currentAccount?.id}
              onChange={(e) => setSelectedAccountId(e.target.value)}
              className="w-full text-xs px-3.5 py-2.5 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-600 transition"
            >
              {accounts.map((acc) => (
                <option key={acc.id} value={acc.id}>
                  {acc.customerReference} — Solde : {acc.balance} {acc.currency} ({acc.status})
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-2 gap-3.5">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Montant ({currentAccount?.currency || 'MAD'})
              </label>
              <input
                type="number"
                step="0.01"
                min="0.01"
                max="1000"
                required
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="w-full text-xs font-mono px-3.5 py-2.5 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-600 transition"
              />
              <span className="text-[10px] text-slate-400 mt-1 block">Max configuré : 1 000 {currentAccount?.currency}</span>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Catégorie Marchand
              </label>
              <select
                value={merchantCategory}
                onChange={(e) => setMerchantCategory(e.target.value)}
                className="w-full text-xs px-3 py-2.5 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-600 transition"
              >
                <option value="TECH">Équipement Tech</option>
                <option value="RETAIL">Commerce de détail</option>
                <option value="GROCERY">Alimentation & Supermarché</option>
                <option value="TRAVEL">Voyage & Transport</option>
                <option value="SERVICES">Services Professionnels</option>
              </select>
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Code Pays (ISO 2 lettres)
            </label>
            <input
              type="text"
              maxLength={2}
              required
              value={country}
              onChange={(e) => setCountry(e.target.value.toUpperCase())}
              className="w-full text-xs font-mono uppercase px-3.5 py-2.5 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-600 transition"
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
              {isSubmitting ? 'Traitement...' : 'Valider la Transaction'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

