# ARCER AI — Android starter project

এটি ARCER-এর প্রথম MVP/source project। Android Studio-তে খুলে build করা যাবে।

## কী আছে
- Premium dark / blue interface
- মাইক্রোফোন permission এবং tap-to-talk speech recognition
- বাংলা Text-to-Speech উত্তর
- Male/Female voice preference toggle (ডিভাইসে থাকা TTS voice-এর প্রাপ্যতার ওপর নির্ভরশীল)
- কয়েকটি প্রাথমিক কমান্ড: সময়, greeting, app-open intent guidance
- Android 8.0+ (API 26+) target; Infinix Hot 30 / Android 13-এ পরীক্ষা করা যেতে পারে

## গুরুত্বপূর্ণ সীমাবদ্ধতা
- "হে ARCER" always-on wake-word detection এখনো অন্তর্ভুক্ত নয়। এই MVP-তে ব্যবহারকারীকে অ্যাপ খুলে Start Listening চাপতে হবে।
- স্ক্রিন বন্ধ অবস্থায় সবসময় মাইক্রোফোন চালু রাখা Android-এর background/microphone permission ও battery policy-র কারণে আলাদা foreground-service এবং wake-word engine চায়।
- Offline AI এখনো পূর্ণ LLM নয়; শুধু সীমিত local command response আছে।
- Online AI/API integration এখনো যোগ করা হয়নি; API key ছাড়া কোনো cloud AI কাজ করবে না।
- Flashlight toggle ও arbitrary phone setting changes পরবর্তী ধাপে permission-aware করে যোগ করতে হবে।

## Build করার নিয়ম
1. ZIP extract করুন।
2. Android Studio খুলে `ARCER-AI` ফোল্ডারটি Open করুন।
3. Gradle sync শেষ হতে দিন।
4. USB debugging চালু করে ফোন connect করুন অথবা emulator নিন।
5. Run ▶ চাপুন।

প্রথমবার চালালে microphone permission দিন। Android Studio-তে JDK 17 ব্যবহার করুন।
