import React, { useState } from 'react';
import { SubjectType, CreateVerificationRequest } from '../types/verification';
import { verificationApi } from '../api/verificationApi';

interface CreateVerificationProps {
  onVerificationCreated: (verificationId: string, identity: string) => void;
}

export const CreateVerification: React.FC<CreateVerificationProps> = ({
  onVerificationCreated,
}) => {
  const [type, setType] = useState<SubjectType>(SubjectType.EMAIL_CONFIRMATION);
  const [identity, setIdentity] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const request: CreateVerificationRequest = {
        subject: {
          identity,
          type,
        },
      };

      const response = await verificationApi.createVerification(request);
      onVerificationCreated(response.id, identity);
      
      // Reset form
      setIdentity('');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create verification');
    } finally {
      setLoading(false);
    }
  };

  const validateInput = () => {
    if (type === SubjectType.EMAIL_CONFIRMATION) {
      return /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(identity);
    } else {
      return /^\+\d{7,15}$/.test(identity);
    }
  };

  const isValid = validateInput();

  return (
    <div className="max-w-md mx-auto mt-8 p-6 bg-white rounded-lg shadow-lg">
      <h2 className="text-2xl font-bold mb-6 text-gray-800">Create Verification</h2>
      
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Type Selection */}
        <div>
          <label htmlFor="type" className="block text-sm font-medium text-gray-700 mb-2">
            Verification Type
          </label>
          <select
            id="type"
            value={type}
            onChange={(e) => setType(e.target.value as SubjectType)}
            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value={SubjectType.EMAIL_CONFIRMATION}>Email Verification</option>
            <option value={SubjectType.MOBILE_CONFIRMATION}>Mobile Verification</option>
          </select>
        </div>

        {/* Identity Input */}
        <div>
          <label htmlFor="identity" className="block text-sm font-medium text-gray-700 mb-2">
            {type === SubjectType.EMAIL_CONFIRMATION ? 'Email Address' : 'Phone Number'}
          </label>
          <input
            id="identity"
            type="text"
            value={identity}
            onChange={(e) => setIdentity(e.target.value)}
            placeholder={
              type === SubjectType.EMAIL_CONFIRMATION
                ? 'example@email.com'
                : '+1234567890'
            }
            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
          <p className="mt-1 text-xs text-gray-500">
            {type === SubjectType.EMAIL_CONFIRMATION
              ? 'Enter a valid email address'
              : 'Enter phone number with country code (e.g., +1234567890)'}
          </p>
        </div>

        {/* Error Message */}
        {error && (
          <div className="p-3 bg-red-50 border border-red-200 rounded-md">
            <p className="text-sm text-red-600">{error}</p>
          </div>
        )}

        {/* Submit Button */}
        <button
          type="submit"
          disabled={!isValid || loading}
          className={`w-full py-3 px-4 rounded-md font-medium transition-colors ${
            !isValid || loading
              ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
              : 'bg-blue-600 text-white hover:bg-blue-700'
          }`}
        >
          {loading ? 'Creating...' : 'Send Verification Code'}
        </button>
      </form>
    </div>
  );
};
