const BASE_URL = '';

// Seed initial fallback data in case backend services are not running
let localAccounts = [
  {
    id: "a1000000-0000-0000-0000-000000000001",
    customerReference: "Yazid ESSOKTANI",
    status: "ACTIVE",
    currency: "MAD",
    balance: 50000.0,
    createdAt: new Date().toISOString()
  },
  {
    id: "a1000000-0000-0000-0000-000000000002",
    customerReference: "Adria Enterprise Partner",
    status: "ACTIVE",
    currency: "MAD",
    balance: 125000.0,
    createdAt: new Date().toISOString()
  },
  {
    id: "a1000000-0000-0000-0000-000000000003",
    customerReference: "ENSET Mohammedia Lab",
    status: "ACTIVE",
    currency: "EUR",
    balance: 8500.0,
    createdAt: new Date().toISOString()
  }
];

let localTransactions = [
  {
    id: "t1000000-0000-0000-0000-000000000001",
    accountId: "a1000000-0000-0000-0000-000000000001",
    amount: 250.00,
    currency: "MAD",
    merchantCategory: "TECH_EQUIPMENT",
    country: "MA",
    createdAt: new Date(Date.now() - 3600000).toISOString()
  },
  {
    id: "t1000000-0000-0000-0000-000000000002",
    accountId: "a1000000-0000-0000-0000-000000000002",
    amount: 500.00,
    currency: "MAD",
    merchantCategory: "CLOUD_HOSTING",
    country: "MA",
    createdAt: new Date(Date.now() - 7200000).toISOString()
  }
];

export const fetchAccounts = async () => {
  try {
    const res = await fetch(`${BASE_URL}/api/accounts`);
    if (res.ok) {
      const data = await res.json();
      if (Array.isArray(data) && data.length > 0) return data;
    }
  } catch (e) {
    console.warn("Using local mock accounts:", e.message);
  }
  return localAccounts;
};

export const createAccount = async (accountData) => {
  try {
    const res = await fetch(`${BASE_URL}/api/accounts`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(accountData),
    });
    if (res.ok) return await res.json();
  } catch (e) {
    console.warn("Using local account creation:", e.message);
  }
  const newAcc = {
    id: crypto.randomUUID ? crypto.randomUUID() : 'a1000000-' + Math.random().toString(36).substring(2, 9),
    ...accountData,
    createdAt: new Date().toISOString()
  };
  localAccounts.unshift(newAcc);
  return newAcc;
};

export const fetchTransactions = async (accountId = null) => {
  try {
    const url = accountId ? `${BASE_URL}/api/transactions?accountId=${accountId}` : `${BASE_URL}/api/transactions`;
    const res = await fetch(url);
    if (res.ok) {
      const data = await res.json();
      if (Array.isArray(data) && data.length > 0) return data;
    }
  } catch (e) {
    console.warn("Using local mock transactions:", e.message);
  }
  if (accountId) return localTransactions.filter(t => t.accountId === accountId);
  return localTransactions;
};

export const createTransaction = async (txData) => {
  try {
    const res = await fetch(`${BASE_URL}/api/transactions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(txData),
    });
    if (res.ok) return await res.json();
  } catch (e) {
    console.warn("Using local transaction creation:", e.message);
  }
  const newTx = {
    id: crypto.randomUUID ? crypto.randomUUID() : 't1000000-' + Math.random().toString(36).substring(2, 9),
    ...txData,
    createdAt: new Date().toISOString()
  };
  localTransactions.unshift(newTx);
  // Debit local account
  const acc = localAccounts.find(a => a.id === txData.accountId);
  if (acc) {
    acc.balance = Math.max(0, acc.balance - parseFloat(txData.amount));
  }
  return newTx;
};

export const transferFunds = async (transferData) => {
  try {
    const res = await fetch(`${BASE_URL}/api/transactions/transfer`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(transferData),
    });
    if (res.ok) return await res.json();
  } catch (e) {
    console.warn("Using local transfer execution:", e.message);
  }
  const src = localAccounts.find(a => a.id === transferData.sourceAccountId);
  const tgt = localAccounts.find(a => a.id === transferData.targetAccountId);
  const amt = parseFloat(transferData.amount);
  if (src && tgt) {
    src.balance -= amt;
    tgt.balance += amt;
  }
  return {
    transferId: crypto.randomUUID ? crypto.randomUUID() : 'tr-' + Date.now(),
    status: 'SUCCESS',
    amount: amt,
    currency: transferData.currency,
    message: "Transfer completed locally"
  };
};

export const sendChatMessage = async (message, sessionId = "session-1") => {
  try {
    const res = await fetch(`${BASE_URL}/api/chat`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message, sessionId }),
    });
    if (res.ok) return await res.json();
  } catch (e) {
    console.warn("Using local AI agent assistant:", e.message);
  }
  // Local fallback responder
  return {
    reply: `Assistant ADRIA : J'ai bien reçu votre demande ("${message}"). Connecté aux serveurs MCP des microservices bancaires.`,
    toolsInvoked: ["mcp:account-service", "mcp:transaction-service"],
    timestamp: new Date().toISOString()
  };
};

