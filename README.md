# OrangeHRM UI Tests

End-to-end UI automation for the [OrangeHRM demo site](https://opensource-demo.orangehrmlive.com/) using **Selenium WebDriver**, **Cucumber (Gherkin)**, **TestNG**, and **Maven**.

---

## ✅ Features

- Login with Admin credentials  
- Navigate to **Admin → User Management**  
- Add new users (role, employee autocomplete, status, username & password)  
- Search by username  
- Delete users (per-row trash action, confirmed by success toast)  
- Robust waits:
  - Toasts like **“Successfully Saved/Deleted”** instead of brittle labels  
  - Table **rows or “No Records Found”** instead of static counts  

---

## 🧰 Tech Stack

- **Java** (JDK 17+ / tested with JDK 23)  
- **Maven**  
- **Selenium 4.25.0** + WebDriverManager  
- **Cucumber JVM 7.x** + **TestNG**  
- **Chrome / Chromedriver**

---

## 📦 Project Structure

```
src
├── main
│   └── java
│       └── pages
│           ├── LoginPage.java
│           ├── AdminPage.java
│           └── AddUserPage.java
└── test
    ├── java
    │   └── steps
    │       ├── Hooks.java
    │       └── OrangeHRMSteps.java
    └── resources
        ├── features
        │   └── orangehrm.feature
        ├── config.example.properties
        └── (config.properties)   # local only, ignored by git
```

---

## ⚙️ Setup

1. **Clone repo**
   ```bash
   git clone https://github.com/<YOUR_USERNAME>/OrangeHRM-SeleniumJava.git
   cd OrangeHRM-SeleniumJava
   ```

2. **Install requirements**
   - JDK 17+  
   - Maven 3.8+  
   - Google Chrome (latest)

3. **Config file**
   Copy the example config and edit with your local values:
   ```bash
   cp src/test/resources/config.example.properties src/test/resources/config.properties
   ```

   Example:
   ```properties
   base.url=https://opensource-demo.orangehrmlive.com/
   admin.username=Admin
   admin.password=admin123
   ```

---

## 🏃 Run Tests

- Run all tests with Maven:
  ```bash
  mvn clean test
  ```

- Run from IntelliJ:
  - Open `orangehrm.feature`
  - Click the **Run** icon in the gutter, or run the Cucumber runner

---

## 📄 .gitignore (already included)

```
.idea/
*.iml
out/
target/
surefire-reports/
test-output/
reports/
screenshots/
src/test/resources/config.properties
*.mp4
*.zip
```

---

## 🚑 Troubleshooting


  ```

- **User “already exists”**: the add form shows an inline error instead of a toast. Delete the user first or use a unique username.

- **Counts don’t match**: pagination can change totals. Primary check is that the searched username is gone.

---

## 🤝 Contributing

1. Fork → Branch → Commit → PR  
2. Keep selectors resilient (avoid brittle XPaths)  
3. Prefer toast/table checks over static labels  

---

## 📜 License

MIT
