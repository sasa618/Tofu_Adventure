テクスチャのサイズは統一する必要がある。
拡張子は.png

[blocks]
ファイル名は整数(0以上)
ファイル名は0, 1, 2,...と、順になるようにする(数字を飛ばすと正常に読み込まれない)
ファイル名はそのままidとなる

[gui]
ボタンなどの部品の画像

[player]
ファイル名(playerTexturesのkeyの値)	: 説明

player_wait.png(0)	: プレイヤーの入力待機状態(止まっている状態)
player_walkL.png(-1) 	: 左に歩く
player_walkR.png(1)	: 右に歩く
player_jumpR.png(10)	: ジャンプ(上昇、右向き)
player_jumpL.png(-10)	: ジャンプ(上昇、左向き)
player_fallR.png(11)	: 落ちる(右向き)
player_fallL.png(-11)	: 落ちる(左向き)