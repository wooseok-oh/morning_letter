# 하루의 힘

매일 아침 7시(KST)를 기준으로, 하루를 살아가는 사람에게 짧은 한국어 편지를 보냅니다.

영문 글귀를 함께 두되, 직역이 어색하면 한국어 문맥에 맞게 풀어 적습니다. 주식·투자 이야기는 넣지 않습니다.

## 주식 브리핑

기존 주식 브리핑은 이 저장소로 옮기지 않았습니다. 그대로 다음 저장소에 있습니다.

https://github.com/wooseok-oh/stock_advisor

## 편지 구성

- 요일에 맞는 짧은 인사
- 영어 글귀, 출처, 한국어 번역, 번역을 그렇게 한 이유
- 오늘을 위한 두세 문단
- 오늘 하나만 해볼 작은 행동
- 맺음말

글귀는 `backend/src/main/resources/letters/catalog.json`에 있습니다. Groq 키가 있으면 같은 글귀를 바탕으로 본문을 그날 새로 쓰고, 실패하면 카탈로그에 적어 둔 글을 그대로 보냅니다.

## 발송

GitHub Actions `Morning Letter`가 매일 06:47 KST에 실행되어 7시 전후 도착을 노립니다. 주말에도 보냅니다.

저장소 Secrets:

- `NAVER_MAIL_USERNAME`
- `NAVER_MAIL_PASSWORD` (네이버 앱 비밀번호)
- `GROQ_API_KEY` (없으면 카탈로그 글로 발송)
- `LETTER_RECIPIENT` (쉼표로 구분, 없으면 기본 수신자)

로컬에서 오늘의 편지를 보려면:

```powershell
cd backend
.\gradlew.bat bootRun
```

브라우저에서 http://localhost:8082/ 를 열면 됩니다. 로컬 실행은 메일을 자동으로 보내지 않습니다.
