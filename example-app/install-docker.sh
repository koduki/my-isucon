#!/bin/bash

# スクリプトをrootユーザーまたはsudo権限で実行する必要があります

# 1. 既存のDockerを削除
apt-get remove -y docker docker-engine docker.io containerd runc

# 2. 必要なパッケージをインストール
apt-get update
apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release

# 3. Dockerの公式GPGキーを追加
curl -fsSL https://download.docker.com/linux/debian/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# 4. Dockerのリポジトリを追加
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 5. Dockerのインストール
apt-get update
apt-get install -y docker-ce docker-ce-cli containerd.io

# 6. Dockerのバージョンを確認
docker --version

# 7. Dockerサービスを起動し、自動起動を有効にする
systemctl start docker
systemctl enable docker

echo "Dockerのインストールが完了しました。"