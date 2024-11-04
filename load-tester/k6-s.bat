@echo off
REM k6-docker.bat

REM 引数を変数に格納
set SCRIPT_PATH=%1

REM バックスラッシュをスラッシュに変換
for /f "tokens=*" %%i in ('echo %SCRIPT_PATH:\=/%') do set SCRIPT_PATH=%%i

REM docker-composeコマンドを実行
docker-compose.exe run load-tester run --vus 1 --iterations 1 /workspace/%SCRIPT_PATH%