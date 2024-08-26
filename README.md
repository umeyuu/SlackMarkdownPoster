
# SlackMarkdownPoster

SlackMarkdownPoster は、特定のフォーマットの Markdown ファイルを Slack に投稿するツールです。このツールを使うことで、Markdown 形式の文書を直接 Slack に投稿し、情報共有を効率的に行うことができます。

## 特徴

- Slackの Incoming Webhook を利用してメッセージを送信。
- カスタマイズ可能な投稿フォーマットにより、見た目を整えて情報を明確に伝えることが可能。
- 特定のセクションを除外する機能により、不要な情報は投稿から除外されます。

## 初期設定

1. **Slack Incoming Webhook の設定**  
   Slack で Incoming Webhook を作成し、得られた Webhook URL を環境変数 `SLACK_WEBHOOK_URL` に設定します。

2. **設定ファイルの編集**  
   `config.yml` ファイルに投稿のフォーマットを記述します。設定項目には、投稿の色やセクション、除外するセクションが含まれます。

## 使い方

以下のコマンドを使用して SlackMarkdownPoster を実行し、Slack に Markdown ファイルを投稿します。

```bash
java -jar SlackMarkdownPoster.jar sample-file/sample.md
```

## `config.yml` の設定例

以下は `config.yml` の設定例です。ここでは、投稿の色、含めるセクションのタイトルと画像、除外するセクションを指定しています。

```yaml
slack:
  color: "#009bbf"  # 投稿の色

exclude:
  - "メモ"
  - "質問"

content:
  title: "content title"  # 投稿のタイトル

  section1:
    title: "H2 title"  # section1のタイトル
    image_url: "https://example.com/image1.jpg"

  section2:
    title: "H3 title"  # section2のタイトル
    image_url: "https://example.com/image2.jpg"
```

この設定により、指定したフォーマットで Slack への投稿が行われ、視覚的に魅力的なメッセージを簡単に作成することができます。
