SVGPrintingReport
=================
このプログラムは、一年分の日記帳を作成するものです。

1. 最初に休日定義 (src/main/resources/syukujitsu.csv ) を更新してください。

  - 休日の (法律上の) 一次情報は内閣府です https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html
  - 年後半になると、来年度の休日が発表されます
  - https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv をダウンロードして、振替休日を追加してください 
    - 「2月12日、4月30日、9月24日、12月24日 は休日となります。」などと書かれています
    ```
    $ curl https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv | iconv -f SJIS -t UTF-8 | tee syukujitsu.csv 
    ```
  - SVGPrintingReport は、来年の日記帳を造ります

2. App.java を実行すると、デスクトップ/Cal に、日記帳の SVG 画像ができます

  ```
  $ mvn package
  $ java -jar target/Calendar.jar
  ```
  
3. createBook.sh を実行します

  - いくつか文書管理アプリが必要です
  - macOS の場合
    - inkscape (macOS の場合 homebrew でインストール)
    - ghostscript (macOS の場合 homebrew でインストール)
    - pdftk (macOS の場合 homebrew 版だめ (2017年現在))
      - http://stackoverflow.com/questions/32505951/pdftk-server-on-os-x-10-11
      - 必要があれば brew unlink pkftk でアンインストールして
      - https://www.pdflabs.com/tools/pdftk-the-pdf-toolkit/pdftk_server-2.02-mac_osx-10.11-setup.pkg をインストール
  - Ubuntu 18 (Linux Mint 19) の場合
    - apt install inkscape ghostscript
    - pdftk は、GCJ の権利関係の問題で apt に入っていないので、自分でビルドする https://at-sushi.com/pukiwiki/index.php?Linux%20pdftk%20for%20Ubuntu18
4. diary.pdf ができるので、あとは普通に両面印刷する
