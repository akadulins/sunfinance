import React, { useState } from 'react';
import { CreateVerification } from './components/CreateVerification';
import { ConfirmVerification } from './components/ConfirmVerification';
import { VerificationSuccess } from './components/VerificationSuccess';
import './App.css';

type AppState = 'create' | 'confirm' | 'success';

function App() {
  const [state, setState] = useState<AppState>('create');
  const [verificationId, setVerificationId] = useState<string>('');
  const [identity, setIdentity] = useState<string>('');

  const handleVerificationCreated = (id: string, identityValue: string) => {
    setVerificationId(id);
    setIdentity(identityValue);
    setState('confirm');
  };

  const handleConfirmed = () => {
    setState('success');
  };

  const handleReset = () => {
    setState('create');
    setVerificationId('');
    setIdentity('');
  };

  const handleBack = () => {
    setState('create');
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 py-12 px-4">
      <div className="max-w-4xl mx-auto">
        <header className="text-center mb-8">
          <h1 className="text-4xl font-bold text-gray-800 mb-2">
            Verification System
          </h1>
          <p className="text-gray-600">
            Secure email and mobile verification
          </p>
        </header>

        {state === 'create' && (
          <CreateVerification onVerificationCreated={handleVerificationCreated} />
        )}

        {state === 'confirm' && (
          <ConfirmVerification
            verificationId={verificationId}
            identity={identity}
            onConfirmed={handleConfirmed}
            onBack={handleBack}
          />
        )}

        {state === 'success' && <VerificationSuccess onReset={handleReset} />}
      </div>
    </div>
  );
}

export default App;