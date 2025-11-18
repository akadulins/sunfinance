import axios, { AxiosError } from 'axios';
import {
  CreateVerificationRequest,
  CreateVerificationResponse,
  ConfirmVerificationRequest,
  ApiError
} from '../types/verification';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const verificationApi = {
  /**
   * Create a new verification
   */
  createVerification: async (
    request: CreateVerificationRequest
  ): Promise<CreateVerificationResponse> => {
    try {
      const response = await apiClient.post<CreateVerificationResponse>(
        '/verifications',
        request
      );
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  },

  /**
   * Confirm a verification with code
   */
  confirmVerification: async (
    verificationId: string,
    request: ConfirmVerificationRequest
  ): Promise<void> => {
    try {
      await apiClient.put(`/verifications/${verificationId}/confirm`, request);
    } catch (error) {
      throw handleApiError(error);
    }
  },
};

/**
 * Handle API errors
 */
function handleApiError(error: unknown): Error {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<ApiError>;
    
    if (axiosError.response?.data) {
      const apiError = axiosError.response.data;
      return new Error(apiError.message || 'An error occurred');
    }
    
    if (axiosError.message) {
      return new Error(axiosError.message);
    }
  }
  
  return new Error('An unexpected error occurred');
}