# CipherLock Definitions

## Master Password
A master password is a primary password used to secure a password manager account. It is the only password a user needs to remember to access all other stored passwords.

## Secure Key
A secure key is a key generated for the user upon account creation that can be used for multi-factor authentication. This key is unique only to the user. This key is not stored in the database whatsoever. This key is only stored on the client side device. The user is responsible for recording this key because authentication of the user's account cannot happen without it.

## Recovery Key
A recovery key is a key generated for the user upon account creation that can be used for resetting the user's password in the event the user forgets their password.
This key is saved to the database, unlike the secure key. This key is used to encrypt the user's RSA private key upon account creation sothat the RSA private key can still be decrypted in the event the user forgets their password. Can only be used to reset the user's password if accompanied with a multi-factor authentication method.

## Password-Derived Symmetric Key
A password-derived symmetric key is a key that is derived from the user's master key upon account creation and login before their password is hashed and stored in the database. This key is used to encrypt and decrypt their RSA private key. 

## RSA Key Pair

## RSA Public Key

## RSA Private Key

## AES Key

## Lock
A lock is a secure storage unit for a user's credentials, passwords, and other sensitive information. Each lock is uniquely associated with a specific user and is encrypted using a symmetric AES key.