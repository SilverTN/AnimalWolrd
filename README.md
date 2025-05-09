## Проект-решение второго модуля JR.

## 📝 Описание проекта
Симулятор экосистемы острова, где животные и растения взаимодействуют по заданным правилам.

## 🎯 Поставленная задача
Создать программу, моделирующую остров с локациями (100×100 клеток), где:

- 🌿 **Растительность** растёт с определённой скоростью
- 🐾 **Животные** (травоядные и хищники) могут:
  - 🍽️ Питаться (растениями/другими животными)
  - 🚶 Передвигаться между локациями
  - ❤️ Размножаться (при наличии пары)
  - 💀 Умирать (от голода/хищников)

---

## ⚙️ Особенности реализации

### 🌱 Инициализация мира
- Создание массива локаций с растительностью и животными
- Настройка начальных параметров через конфигурационный файл

### 🔄 Цикл симуляции
Пошаговое выполнение на каждом "тике":
1. 🍽️ Питание
2. ❤️ Размножение  
3. 🚶 Перемещение  
4. ☠️ Проверка состояния здоровья

### ⚡ Параллелизм
- Многопоточная обработка (ExecutorService)
- Параллельное обновление локаций

### 📊 Логирование
- Вывод в консоль
- Запись в файл (опционально в logback-spring.xml)
- Фиксация ключевых событий

## 🛠️ Технологический стек

| Категория       | Технологии                          |
|----------------|-----------------------------------|
| **Backend**    | Spring Boot, WebFlux, Multithreading, JSON, SSE |
| **Frontend**   | HTML/CSS, JavaScript, D3.js       |

### ⚡ SSE (Server-Sent Events)
▸ Потоковая передача данных сервер→клиент  
▸ Обновление состояния экосистемы в реальном времени

## 🌐 Визуализация
Доступ через:  
`http://localhost:8080/`

🖥️ **UA**  
![image](https://github.com/user-attachments/assets/ee9048c7-1f6c-4963-b70b-8e769524bc0c)

### 📋 Логирование в Console
![image](https://github.com/user-attachments/assets/6491aa84-8a42-484b-b54b-13d4b98e96ca)




