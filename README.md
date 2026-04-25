# ContactManagementSystem

`ContactManagementSystem` is a Java Swing application used to store and manage contact details such as name, phone number, and email address with file saving support.

## Features

- Add new contacts
- View all saved contacts in a table
- Search contacts by name, phone number, or email
- Update selected contact details
- Delete contacts with confirmation
- Auto-save contacts to `contacts.txt`

## Technologies Used

- Java
- Java Swing
- AWT Graphics for custom UI styling
- File handling using `contacts.txt`

## Project Files

- `ContactManagementSystem.java` - main source code file
- `contacts.txt` - stores saved contact records

## How to Run

1. Make sure Java is installed on your system.
2. Open a terminal in the project folder.
3. Compile the program:

```bash
javac ContactManagementSystem.java
```

4. Run the program:

```bash
java ContactManagementSystem
```

## How to Use

1. Enter contact name, phone number, and email address.
2. Click `ADD CONTACT` to save a new contact.
3. Use the search box to find contacts quickly.
4. Select a contact from the table to edit or delete it.
5. Click `UPDATE` to save changes.
6. Click `DELETE` to remove the selected contact.
7. Click `CLEAR` to reset the input fields.

## Validation Rules

- All fields are required
- Phone number must be in a valid format
- Email must contain `@` and `.`

## Data Storage

- Contacts are saved in `contacts.txt`
- Each contact is stored in this format:

```text
Name|Phone|Email
```

## Learning Outcome

This project helps practice:

- Java Swing GUI development
- Table handling using `JTable`
- CRUD operations
- Search and filtering
- File reading and writing in Java

## Author

Created as a Java mini project for practice and learning.
