# README

## テストの実行

```
docker-compose.exe run load-tester run --vus 1 --iterations 1 /workspace/tests/functional-test.js
.\k6-s.bat .\tests\functional-test.js 
.\k6.bat .\tests\load-test.js 
```