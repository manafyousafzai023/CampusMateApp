# CampusMate — Android App with Common Screens

Assignment 2 · Android Application Development (Java + XML)

CampusMate is a small student-portal app that implements the common screens found in most Android apps: splash, login, registration, password recovery, a dashboard of modules and a profile page. Authentication is simulated locally (no backend).

## Demo login

| Field | Value |
|---|---|
| Email | `student@campusmate.edu` |
| Username | `student` |
| Password | `Student@123` |

You can also register a new account and log in with it. Accounts are stored on the device with `SharedPreferences`.

## Screens and features

| Screen | Activity | What it does |
|---|---|---|
| Splash | `SplashActivity` | Shows the logo, app name and tagline for 2 seconds, then opens Login. |
| Login | `LoginActivity` | Email/username + password, Login button, links to Register and Forgot Password. Validates empty fields, email format and wrong credentials. |
| Register | `RegisterActivity` | Name, email, password, confirm password. Validates name, email format, duplicate email, password strength and matching passwords. Returns to Login with the email pre-filled. |
| Forgot Password | `ForgotPasswordActivity` | Accepts an email, checks it exists, shows a "sending" indicator and a dialog confirming a (simulated) recovery link. |
| Reset Password | `ResetPasswordActivity` | New password + confirm password with the same strength rules, and rejects reusing the old password. Saves it and returns to Login. |
| Dashboard | `DashboardActivity` | Greeting, today's date, semester banner and six module cards (Courses, Timetable, Attendance, Results, Notices, Profile) in a grid, each with an icon and text. |
| Profile | `ProfileActivity` | User's initials avatar, name, username, email and account type, a Dark theme switch, and Logout (with confirmation). |

Password rules: at least 8 characters, at least one letter and one number, no spaces.

## Navigation flow

```
Splash ──(2 s)──► Login ──(valid login)──► Dashboard ──► Profile ──(Logout)──► Login
                   │  ▲                       │
                   │  │                       └── module cards (Toast for demo modules)
                   │  └──(account created)── Register
                   │
                   └──► Forgot Password ──(link "sent")──► Reset Password ──(saved)──► Login
```

- Every move between screens uses an **explicit Intent** (`new Intent(this, TargetActivity.class)`).
- Data is passed with Intent extras (email to Dashboard/Profile/Reset, pre-filled email and messages back to Login).
- Register returns its result to Login with `registerForActivityResult`.
- Reset Password returns to Login with `FLAG_ACTIVITY_CLEAR_TOP`, so Forgot/Reset leave the back stack.
- Logout uses `FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK`, so Back cannot reopen the dashboard.

## How each technical requirement is met

| Requirement | Where |
|---|---|
| Default and alternative resources | `values/strings.xml` (all UI text), `values/colors.xml` + `values-night/colors.xml` (night-mode colours), `values/bools.xml` + `values-night/bools.xml` (status-bar icons), `values/integers.xml` + `values-land/integers.xml` (2 grid columns in portrait, 3 in landscape) |
| LinearLayout | Register, Forgot Password and Reset Password screens; form stacks and info rows |
| GridLayout | Dashboard module grid (`activity_dashboard.xml`) |
| ConstraintLayout | Splash, Login, Dashboard and Profile screens |
| Text / email / password controls | `TextInputEditText` with `textPersonName`, `textEmailAddress` and `textPassword` input types, password show/hide toggle |
| Button clicks and validation | All buttons use click listeners; field errors shown with `TextInputLayout.setError()`, shared rules in `ValidationUtils` |
| Explicit Intents | Every screen change (see flow above) |
| Consistent UI | One Material 3 theme, shared styles for inputs, buttons, cards and text, a shared back button layout (`view_back_button.xml`) |

## Project structure

```
app/src/main/
├── AndroidManifest.xml
├── java/com/campusmate/
│   ├── CampusMateApp.java          applies saved light/dark choice
│   ├── SplashActivity.java
│   ├── LoginActivity.java
│   ├── RegisterActivity.java
│   ├── ForgotPasswordActivity.java
│   ├── ResetPasswordActivity.java
│   ├── DashboardActivity.java
│   ├── ProfileActivity.java
│   ├── UserStore.java              simulated local accounts + session
│   └── ValidationUtils.java        shared validation rules
└── res/
    ├── layout/                     one XML layout per screen
    ├── drawable/                   vector icons and shapes
    ├── values/                     strings, colors, themes, dimens, integers, bools
    ├── values-night/               dark-mode colors and bools
    └── values-land/                landscape grid column count
```

## How to run

1. Open the `CampusMate` folder in Android Studio (Hedgehog or newer).
2. Let Gradle sync (Android Gradle Plugin 8.5.2, Gradle 8.7, compileSdk 34, minSdk 24).
3. Run on an emulator or phone and log in with the demo account.

To see the night-mode resources, turn on **Dark theme** in the Profile screen (or switch the device to dark mode). Rotate to landscape to see the 3-column grid.

## Notes

- Authentication and password recovery are simulated; no email is actually sent.
- Passwords are stored in plain text in `SharedPreferences` because this is a classroom simulation. A real app would use a backend and hashed passwords.
