export enum SubjectType {
  EMAIL_CONFIRMATION = 'email_confirmation',
  MOBILE_CONFIRMATION = 'mobile_confirmation'
}

export interface Subject {
  identity: string;
  type: SubjectType;
}

export interface CreateVerificationRequest {
  subject: Subject;
}

export interface CreateVerificationResponse {
  id: string;
}

export interface ConfirmVerificationRequest {
  code: string;
}

export interface VerificationResponse {
  id: string;
  subjectIdentity: string;
  subjectType: string;
  code: string;
  expiresAt: string;
  confirmed: boolean;
}

export interface ApiError {
  status: number;
  message: string;
  path: string;
  timestamp: string;
}