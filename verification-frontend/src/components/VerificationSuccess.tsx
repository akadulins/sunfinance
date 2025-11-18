import React from 'react';

interface VerificationSuccessProps {
  onReset: () => void;
}

export const VerificationSuccess: React.FC<VerificationSuccessProps> = ({ onReset }) => {
  return (
    <div className="max-w-md mx-auto mt-8 p-6 bg-white rounded-lg shadow-lg text-center">
      <div className="mb-6">
        <div className="mx-auto w-16 h-16 bg-green-100 rounded-full flex items-center justify-center">
          <svg
            className="w-10 h-10 text-green-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M5 13l4 4L19 7"
            />
          </svg>
        </div>
      </div>

      <h2 className="text-2xl font-bold mb-2 text-gray-800">Verification Successful!</h2>
      <p className="text-gray-600 mb-6">Your verification has been confirmed.</p>

      <button
        onClick={onReset}
        className="w-full py-3 px-4 rounded-md font-medium bg-blue-600 text-white hover:bg-blue-700 transition-colors"
      >
        Create New Verification
      </button>
    </div>
  );
};