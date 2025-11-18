import React, { useState, useEffect } from 'react';
import { verificationApi } from '../api/verificationApi';

interface ConfirmVerificationProps {
  verificationId: string;
  identity: string;
  onConfirmed: () => void;
  onBack: () => void;
}

export const ConfirmVerification: React.FC<ConfirmVerificationProps> = ({
  verificationId,
  identity,
  onConfirmed,
  onBack,
}) => {
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [timeLeft, setTimeLeft] = useState(300); // 5 minutes

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await verificationApi.confirmVerification(verificationId, { code });
      onConfirmed();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to confirm verification');
    } finally {
      setLoading(false);
    }
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const isExpired = timeLeft === 0;

  return (
    <div className="max-w-md mx-auto mt-8 p-6 bg-white rounded-lg shadow-lg">
      <h2 className="text-2xl font-bold mb-4 text-gray-800">Confirm Verification</h2>
      
      <div className="mb-6 p-4 bg-blue-50 rounded-md">
        <p className="text-sm text-gray-700">
          A verification code has been sent to:
        </p>
        <p className="font-semibold text-blue-600">{identity}</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Code Input */}
        <div>
          <label htmlFor="code" className="block text-sm font-medium text-gray-700 mb-2">
            Verification Code
          </label>
          <input
            id="code"
            type="text"
            value={code}
            onChange={(e) => setCode(e.target.value.replace(/\D/g, '').slice(0, 8))}
            placeholder="00000000"
            maxLength={8}
            className="w-full px-4 py-3 text-center text-2xl font-bold tracking-widest border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        {/* Timer */}
        <div className="text-center">
          <p className={`text-sm ${isExpired ? 'text-red-600' : 'text-gray-600'}`}>
            {isExpired ? (
              'Code expired'
            ) : (
              <>Time remaining: <span className="font-bold">{formatTime(timeLeft)}</span></>
            )}
          </p>
        </div>

        {/* Error Message */}
        {error && (
          <div className="p-3 bg-red-50 border border-red-200 rounded-md">
            <p className="text-sm text-red-600">{error}</p>
          </div>
        )}

        {/* Buttons */}
        <div className="space-y-2">
          <button
            type="submit"
            disabled={code.length !== 8 || loading || isExpired}
            className={`w-full py-3 px-4 rounded-md font-medium transition-colors ${
              code.length !== 8 || loading || isExpired
                ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                : 'bg-blue-600 text-white hover:bg-blue-700'
            }`}
          >
            {loading ? 'Verifying...' : 'Verify Code'}
          </button>

          <button
            type="button"
            onClick={onBack}
            className="w-full py-3 px-4 rounded-md font-medium bg-gray-200 text-gray-700 hover:bg-gray-300 transition-colors"
          >
            Back
          </button>
        </div>
      </form>
    </div>
  );
};