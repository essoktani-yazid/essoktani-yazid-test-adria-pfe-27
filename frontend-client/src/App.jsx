import React, { useState, useEffect } from 'react';
import Header from './components/Header';
import BentoMetrics from './components/BentoMetrics';
import AccountsSection from './components/AccountsSection';
import TransactionsSection from './components/TransactionsSection';
import NewAccountModal from './components/Modals/NewAccountModal';
import NewTransactionModal from './components/Modals/NewTransactionModal';
import TransferModal from './components/Modals/TransferModal';
import AiChatDrawer from './components/Drawer/AiChatDrawer';
import FloatingActionButton from './components/FloatingActionButton';
import { fetchAccounts, fetchTransactions, createAccount, createTransaction, transferFunds } from './services/api';
import { CheckCircle2 } from 'lucide-react';

export default function App() {
  const [accounts, setAccounts] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [isAccountModalOpen, setIsAccountModalOpen] = useState(false);
  const [isTransactionModalOpen, setIsTransactionModalOpen] = useState(false);
  const [isTransferModalOpen, setIsTransferModalOpen] = useState(false);
  const [isAiDrawerOpen, setIsAiDrawerOpen] = useState(false);
  const [toastMessage, setToastMessage] = useState('');

  const loadData = async () => {
    const accs = await fetchAccounts();
    setAccounts(accs);
    const txs = await fetchTransactions();
    setTransactions(txs);
  };

  useEffect(() => {
    loadData();
  }, []);

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(''), 3500);
  };

  const handleCreateAccount = async (accountData) => {
    await createAccount(accountData);
    await loadData();
    showToast('Nouveau compte bancaire créé avec succès !');
  };

  const handleCreateTransaction = async (txData) => {
    await createTransaction(txData);
    await loadData();
    showToast('Transaction enregistrée et événement Kafka diffusé !');
  };

  const handleTransfer = async (transferData) => {
    await transferFunds(transferData);
    await loadData();
    showToast('Virement exécuté avec succès entre les deux comptes !');
  };

  return (
    <div className="min-h-screen w-full flex flex-col bg-slate-50 text-slate-900 m-0 p-0 antialiased font-sans selection:bg-teal-500 selection:text-white relative">
      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed top-20 right-6 z-50 flex items-center gap-2 px-4 py-2.5 rounded-xl bg-teal-600 text-white text-xs shadow-lg animate-modal-in font-medium">
          <CheckCircle2 className="w-4 h-4 flex-shrink-0" />
          <span>{toastMessage}</span>
        </div>
      )}

      {/* Header */}
      <Header />

      {/* Main Body */}
      <main className="w-full max-w-7xl mx-auto px-4 sm:px-6 py-6 flex-1">
        {/* Bento Grid Metrics */}
        <BentoMetrics accounts={accounts} transactions={transactions} />

        {/* Bank Accounts Section */}
        <AccountsSection
          accounts={accounts}
          onOpenNewAccount={() => setIsAccountModalOpen(true)}
          onOpenTransfer={() => setIsTransferModalOpen(true)}
        />

        {/* Transactions Section */}
        <TransactionsSection
          transactions={transactions}
          onOpenNewTransaction={() => setIsTransactionModalOpen(true)}
        />
      </main>

      {/* Modals */}
      <NewAccountModal
        isOpen={isAccountModalOpen}
        onClose={() => setIsAccountModalOpen(false)}
        onCreate={handleCreateAccount}
      />

      <NewTransactionModal
        isOpen={isTransactionModalOpen}
        onClose={() => setIsTransactionModalOpen(false)}
        accounts={accounts}
        onCreate={handleCreateTransaction}
      />

      <TransferModal
        isOpen={isTransferModalOpen}
        onClose={() => setIsTransferModalOpen(false)}
        accounts={accounts}
        onTransfer={handleTransfer}
      />

      {/* Lateral AI Chat Drawer */}
      <AiChatDrawer
        isOpen={isAiDrawerOpen}
        onClose={() => setIsAiDrawerOpen(false)}
      />

      {/* Floating Action Button */}
      <FloatingActionButton onClick={() => setIsAiDrawerOpen(true)} />
    </div>
  );
}

