import React, { useState, useRef, useEffect } from 'react';
import { X, Send, Bot, Sparkles, Cpu, CheckCircle2 } from 'lucide-react';
import { sendChatMessage } from '../../services/api';

export default function AiChatDrawer({ isOpen, onClose }) {
  const [messages, setMessages] = useState([
    {
      sender: 'bot',
      text: "Bonjour ! Je suis l'Assistant Bancaire Intelligent ADRIA. Connecté aux serveurs MCP des microservices, je peux consulter vos comptes, exécuter des virements et lister les transactions.",
      tools: ["mcp:account-service", "mcp:transaction-service"],
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    }
  ]);
  const [inputPrompt, setInputPrompt] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    if (isOpen) {
      scrollToBottom();
    }
  }, [messages, isOpen]);

  if (!isOpen) return null;

  const handleSend = async (messageText = null) => {
    const textToSend = messageText || inputPrompt;
    if (!textToSend.trim() || loading) return;

    const userMsg = {
      sender: 'user',
      text: textToSend,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };

    setMessages(prev => [...prev, userMsg]);
    if (!messageText) setInputPrompt('');
    setLoading(true);

    try {
      const resp = await sendChatMessage(textToSend);
      const botMsg = {
        sender: 'bot',
        text: resp.reply,
        tools: resp.toolsInvoked || [],
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      };
      setMessages(prev => [...prev, botMsg]);
    } catch (e) {
      setMessages(prev => [
        ...prev,
        {
          sender: 'bot',
          text: "Désolé, une erreur de communication est survenue avec le service Agentic Chatbot.",
          time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const samplePrompts = [
    "Affiche la liste de tous mes comptes",
    "Quel est le solde de mon compte principal ?",
    "Affiche l'historique récent des transactions",
    "Fais un virement de test"
  ];

  return (
    <div className="fixed inset-0 z-50 flex justify-end bg-slate-900/30 backdrop-blur-[2px] animate-fade-in">
      <div className="w-full sm:w-[480px] h-full bg-white border-l border-slate-200 shadow-2xl flex flex-col animate-drawer-in">
        {/* Drawer Header */}
        <div className="px-5 py-4 border-b border-slate-200 flex items-center justify-between bg-slate-50/80">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-teal-600 text-white flex items-center justify-center shadow-xs">
              <Bot className="w-4 h-4" />
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <h3 className="font-semibold text-slate-900 text-sm">Assistant Agentic IA</h3>
                <span className="text-[10px] px-1.5 py-0.2 rounded bg-purple-50 text-purple-700 border border-purple-200 font-mono font-medium">
                  MCP Tools
                </span>
              </div>
              <p className="text-[11px] text-slate-500">Automatisation des opérations bancaires</p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition active:scale-95"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Message Stream */}
        <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-slate-50/50">
          {messages.map((msg, idx) => (
            <div
              key={idx}
              className={`flex flex-col ${msg.sender === 'user' ? 'items-end' : 'items-start'} animate-message-in`}
            >
              <div
                className={`max-w-[88%] rounded-2xl px-4 py-3 text-xs leading-relaxed ${
                  msg.sender === 'user'
                    ? 'bg-teal-600 text-white shadow-xs rounded-br-none'
                    : 'bg-white border border-slate-200/90 text-slate-800 shadow-sm rounded-bl-none'
                }`}
              >
                <div className="whitespace-pre-wrap">{msg.text}</div>

                {/* MCP Tool Badges */}
                {msg.tools && msg.tools.length > 0 && (
                  <div className="mt-2.5 pt-2 border-t border-slate-100 flex flex-wrap gap-1">
                    {msg.tools.map((t, tidx) => (
                      <span
                        key={tidx}
                        className="inline-flex items-center gap-1 text-[10px] px-1.5 py-0.5 rounded bg-slate-100 text-slate-600 font-mono"
                      >
                        <Cpu className="w-2.5 h-2.5 text-teal-600" />
                        {t}
                      </span>
                    ))}
                  </div>
                )}
              </div>
              <span className="text-[10px] text-slate-400 mt-1 px-1 font-mono">
                {msg.time}
              </span>
            </div>
          ))}

          {loading && (
            <div className="flex items-center gap-2 text-xs text-slate-500 p-2 animate-pulse">
              <Sparkles className="w-4 h-4 text-teal-600 animate-spin" />
              <span>L'agent consulte les serveurs MCP...</span>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        {/* Suggested Quick Prompts */}
        <div className="p-3 border-t border-slate-100 bg-white">
          <div className="flex items-center gap-1.5 overflow-x-auto pb-1 text-[11px]">
            {samplePrompts.map((p, pidx) => (
              <button
                key={pidx}
                onClick={() => handleSend(p)}
                className="whitespace-nowrap px-2.5 py-1 rounded-full bg-slate-100 hover:bg-slate-200 active:scale-95 text-slate-600 transition"
              >
                {p}
              </button>
            ))}
          </div>
        </div>

        {/* Input Bar */}
        <div className="p-3 border-t border-slate-200 bg-white">
          <form
            onSubmit={(e) => {
              e.preventDefault();
              handleSend();
            }}
            className="flex items-center gap-2"
          >
            <input
              type="text"
              placeholder="Posez une question ou demandez une action..."
              value={inputPrompt}
              onChange={(e) => setInputPrompt(e.target.value)}
              className="flex-1 text-xs px-3.5 py-2.5 rounded-xl border border-slate-300 focus:outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-600 transition"
            />
            <button
              type="submit"
              disabled={loading || !inputPrompt.trim()}
              className="p-2.5 rounded-xl bg-teal-600 hover:bg-teal-700 active:scale-95 text-white transition disabled:opacity-40 shadow-xs"
            >
              <Send className="w-4 h-4" />
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

