# CipherLock Backend Design

Author: Parker Harrelson  
Repository: https://github.com/ParkerHarrelson/Password-Manager

## Brief Overview

The goal of this system is to provide users with a "lockbox" for securely storing credentials, passwords, and other sensitive information, ensuring it remains protected from potential attackers.

Typical password management practices involve writing passwords down in notes or reusing the same passwords across multiple accounts, both of which are cumbersome and highly insecure. A password manager allows users to create an account secured with a master password known only to them. Within their account, they can store numerous credentials, all of which are encrypted and protected from attackers.

However, this introduces a single point of failure: the user. If the user creates a weak master password or forgets it, they risk losing access to their stored information or having their account compromised, exposing all their data.

Despite this, the added security provided by a password manager significantly outweighs the risk of the master password being breached. By promoting the use of strong, unique passwords for each account and securely storing them, the password manager enhances overall security and reduces the likelihood of unauthorized access.

## Security Architecture


### Account Creation

Upon account creation, a user will provide their name, email address, username, and a [master password](definitions.md#master-password). During this process, several key steps are performed to ensure the security of the user's account:

1. [RSA Key Pair](definitions.md#rsa-key-pair) Generation: A unique RSA Key Pair is generated for the user.
2. Salt Generation: A unique salt is generated for the user.
3. [Secure Key](definitions.md#secure-key) Generation: A 128-bit secure key is generated using a cryptographically secure random number generator. This key is generated on the client-side device and is never stored in the database. The user is responsible for recording this key for future logins on other devices.
4. Symmetric Key Derivation: The salt, master password, and secure key are combined, and a symmetric key is derived using PBKDF2. This [password-derived symmetric key](definitions.md#password-derived-symmetric-key) is used to encrypt the user's [RSA Private Key](definitions.md#rsa-private-key).
5. Key Storage: The [RSA Public Key](definitions.md#rsa-public-key), the encrypted RSA Private Key, and the salt are stored in the database. The secure key is stored securely on the user's device.
6. Password Storage: The salt-password combination is hashed using SHA-256 and stored in the database, along with the salt in its own field.

Another critical process during account creation is the generation of a [recovery key](definitions.md#recovery-key). The recovery key is generated along with a unique salt. The recovery key and its salt are hashed usingSHA-256, and the resulting hash is stored in the database. The recovery key itself is not stored in the database and must be recorded by the user. This key, in combination with its salt, is used to encrypt the RSA Private Key, and the result is stored in the database alongside the password-derived encrypted RSA key. This results in two encrypted versions of the user's private key in the database. If the user forgets their password, the recovery key can be used to decrypt their RSA private key. The user can then provide a new password, which will generate a new password-derived symmetric key to re-encrypt the RSA private key.

The user will have the option, during account creation, to enable whichever multi-factor authentication methods they want, but will have to pick at least two different options.

### Password Reset

Master password reset can occur in two ways: by providing the current master password or by using the recovery key. In either case, the user must also authenticate using two-factor authentication before setting a new password. The new password is combined with a new salt, hashed, and stored in the database.

If the user loses their recovery key, they can generate a new one by providing their current password. This password is used to decrypt the RSA Private Key, which is then re-encrypted with the new recovery key.

In the event that the user loses both their password and recovery key, they can be locked out of their account for a specified duration or authenticate using two of their multi-factor authentication options. However, without either the master password or recovery key, they cannot access their stored credentials, as there is no way to decrypt their RSA Private Key. It is therefore the user's responsibility to safeguard both their recovery key and master password.

### Authentication

Security is of the utmost importance for a password manager, so there will be a requirement that the user uses at least two-factor authentication, with the choice of using multi-factor authentication. We will give the user the freedom to choose which multi-factor authentication methods they wants available to them, but they must have at least two. Some of these options include, but are not limited to:

1. Email Verification
2. Phone Verification
4. Authenticator App of User's Choosing

When the user attempts to log in, the following process will take place to ensure secure authentication:

1. Extract Secure Key: The application first attempts to retrieve the secure key from the user's device.
    * If the Secure Key is Present:
        * The secure key is combined with the provided password and the stored salt from the database.
        * A symmetric key is derived using PBKDF2.
    * If the Secure Key is Not Present:
        * The user will be prompted to provide their secure key manually.
        * The provided secure key is combined with the password and salt to derive the symmetric key using PBKDF2.
2. Hash Comparison:
    * The derived symmetric key is used to hash the provided password with the stored salt using SHA-256.
    * The resulting hash is compared with the stored hash in the database.
    * If the hashes do not match, authentication fails.
3. Multi-factor Authentication:
    * If the hashes match, the user will be prompted to choose their two/multi-factor verification option(s).
    * The user must successfully complete the chosen multi-factor authentication methods to proceed.
4. Final Authentication:
    * If the multi-factor authentication is successful, the user is authenticated and granted access to their account.

### User Locks

The contents that the user stores inside the password manager are called [locks](definitions.md#lock). A lock is an entity that stores sensitive information that the user wants to protect from attackers. The contents of a lock can include various items such as a username, email, URL for a website, and most importantly, the password the user is trying to store. However, merely storing the password is not secure, as an attacker could potentially breach the database and steal the user's sensitive information. Therefore, we must ensure that not only is the lockbox secured, but so are the locks inside. The following steps ensure the security of the locks within the lockbox:

1. User Creates Lock: A new lock is created, and the user adds any necessary information, including but not limited to a password, username, and email.
2. AES Key Creation: A new and unique AES key is generated.
3. AES Encryption: When the lock is created, the selected contents are encrypted using the generated AES key and the AES-256 algorithm.
4. AES Key Encrypted: After encrypting the lock's contents, the AES key itself is encrypted using the user's RSA Public Key.
5. Lock Storage: The encrypted contents of the lock and the encrypted AES key are stored in the database.

When a user wants to view the contents of a lock, the following decryption process takes place:

1. AES Key Decryption: The AES key used to encrypt the lock is decrypted using the user's RSA Private Key.
2. Lock Decryption: The now decrypted AES key is used to decrypt the contents of the lock, allowing the user to view them.

### Database Layout

![Database Layout Version 1.1](../database/CipherLock%20Database%20ERR%20Design%201.1.png)

### Webservices

These webservices are responsible for handling all backend logic for CipherLock. These webservices can be interacted with via the ***cipherlock_api***.

#### ws_cipherlock_auth

#### ws_cipherlock_user

#### ws_cipherlock_key_management

#### ws_cipherlock_locks

#### ws_cipherlock_mfa

#### ws_cipherlock_notifications

### API

#### cipherlock_api

### Libraries

#### lib_cipherlock_crypto

#### lib_cipherlock_entity

#### lib_cipherlock_dto
