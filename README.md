SVGPrintingReport
=================
このプログラムは、一年分の日記帳を作成するものです。

1. 最初に休日定義 (src/main/resources/syukujitsu.csv ) を更新してください。

  - 休日の (法律上の) 一次情報は内閣府です http://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html
  - 年後半になると、来年度の休日が発表されます
  - http://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv をダウンロードして、振替休日を追加してください
  - SVGPrintingReport は、4 列目 (来年) の休日定義の最初の日 (元日) の年の日記帳を造ります

1. App.java を実行すると、デスクトップ/Cal に、日記帳の SVG 画像ができます

1. createBook.sh を実行します

  - いくつか文書管理アプリが必要です
  - inkscape (macOS の場合 homebrew でインストール)
  - ghostscript (macOS の場合 homebrew でインストール)
  - pdftk (macOS の場合 homebrew 版だめ (2017年現在))
    - http://stackoverflow.com/questions/32505951/pdftk-server-on-os-x-10-11
    - 必要があれば brew unlink pkftk でアンインストールして
    - https://www.pdflabs.com/tools/pdftk-the-pdf-toolkit/pdftk_server-2.02-mac_osx-10.11-setup.pkg をインストール

1. diary.pdf ができるので、あとは普通に両面印刷する
