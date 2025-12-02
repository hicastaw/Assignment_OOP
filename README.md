# ♟️ Chess PTIT

Ứng dụng cờ vua hai người chơi được xây dựng bằng Java Swing, tuân thủ đầy đủ luật cờ vua quốc tế và lưu trữ lịch sử các ván đấu.

## 📋 Mục lục

- [Giới thiệu](#giới-thiệu)
- [Tính năng](#tính-năng)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
- [Cài đặt](#cài-đặt)
- [Hướng dẫn sử dụng](#hướng-dẫn-sử-dụng)
- [Kiến trúc dự án](#kiến-trúc-dự-án)
- [Tài liệu tham khảo](#tài-liệu-tham-khảo)

## 🎯 Giới thiệu

Chess PTIT là một ứng dụng cờ vua desktop được phát triển như một dự án Lập trình Hướng đối tượng (OOP). Ứng dụng cung cấp trải nghiệm chơi cờ vua đầy đủ cho hai người chơi trên cùng một máy tính, với giao diện đồ họa trực quan và khả năng lưu trữ lịch sử ván đấu.

### Mục tiêu dự án

- Áp dụng các nguyên lý OOP: Kế thừa, Đa hình, Đóng gói, Trừu tượng
- Xây dựng ứng dụng desktop với Java Swing
- Tích hợp cơ sở dữ liệu SQLite để lưu trữ dữ liệu
- Triển khai đầy đủ luật cờ vua quốc tế

## ✨ Tính năng

### Chức năng chính

- **Chơi cờ vua hai người**: 
  - Bàn cờ 8x8 với đầy đủ quân cờ (Vua, Hậu, Xe, Tượng, Mã, Tốt)
  - Nhập tên người chơi trước khi bắt đầu
  - Hiển thị lượt chơi hiện tại
  
- **Tuân thủ luật cờ vua**:
  - Kiểm tra nước đi hợp lệ cho từng loại quân
  - Phát hiện và cảnh báo trạng thái chiếu (Check)
  - Phát hiện chiếu hết (Checkmate) và hòa (Stalemate)
  - Hỗ trợ nhập thành (Castling)
  - Hỗ trợ bắt tốt qua đường (En Passant)
  - Phong cấp tốt (Pawn Promotion)

- **Quản lý lịch sử**:
  - Lưu trữ tự động mỗi ván đấu
  - Xem danh sách các trận đã chơi
  - Xem chi tiết từng nước đi của mỗi trận

### Giao diện

- Giao diện đồ họa thân thiện với Swing
- Màu sắc rõ ràng phân biệt quân và bàn cờ
- Hiển thị trạng thái game real-time
- Chuyển đổi linh hoạt giữa các màn hình

## 🛠️ Công nghệ sử dụng

- **Ngôn ngữ**: Java 21
- **Build Tool**: Apache Maven
- **GUI Framework**: Java Swing
- **Cơ sở dữ liệu**: SQLite 3.45.3.0
- **JDBC Driver**: Xerial SQLite JDBC
- **Testing**: JUnit 4.11

## 💻 Yêu cầu hệ thống

- **Java Development Kit (JDK)**: Phiên bản 21 trở lên
- **Apache Maven**: Phiên bản 3.6 trở lên
- **Hệ điều hành**: Windows, macOS, hoặc Linux
- **RAM**: Tối thiểu 512 MB
- **Dung lượng ổ đĩa**: 50 MB

## 📦 Cài đặt

### 1. Clone repository

```bash
git clone https://github.com/hicastaw/Assignment_OOP.git
cd Assignment_OOP/demo
```

### 2. Build dự án với Maven

```bash
mvn clean install
```

### 3. Chạy ứng dụng

```bash
mvn exec:java -Dexec.mainClass="com.ChessPTIT.main.Main"
```

Hoặc chạy trực tiếp từ file `.class`:

```bash
cd target/classes
java com.ChessPTIT.main.Main
```

### 4. Cơ sở dữ liệu

Ứng dụng tự động tạo cơ sở dữ liệu SQLite tại:
```
%USERPROFILE%\.ChessPTIT\chess_history.db (Windows)
~/.ChessPTIT/chess_history.db (macOS/Linux)
```

## 📖 Hướng dẫn sử dụng

### Bắt đầu ván mới

1. Khởi động ứng dụng
2. Chọn **"New Game"** từ menu chính
3. Nhập tên cho Player 1 (Quân Trắng) và Player 2 (Quân Đen)
4. Nhấn **"Start Game"** để bắt đầu

### Chơi cờ

1. **Chọn quân**: Click vào quân cờ của bạn (theo lượt)
2. **Di chuyển**: Click vào ô đích hợp lệ (được highlight)
3. **Hủy chọn**: Click lại vào quân đang chọn hoặc click vào ô không hợp lệ
4. Lượt chơi tự động chuyển sau mỗi nước đi hợp lệ

### Các nước đi đặc biệt

- **Nhập thành**: Di chuyển Vua 2 ô về phía Xe (khi các điều kiện nhập thành thỏa mãn)
- **Bắt tốt qua đường**: Di chuyển Tốt chéo vào ô trống khi tốt địch vừa đi 2 ô
- **Phong cấp**: Khi Tốt đến hàng cuối, chọn quân để phong cấp (Hậu, Xe, Tượng, hoặc Mã)

### Xem lịch sử

1. Chọn **"View History"** từ menu chính
2. Chọn trận đấu từ danh sách
3. Xem chi tiết các nước đi đã thực hiện

## 🏗️ Kiến trúc dự án

### Mô hình thiết kế: MVC + DAO

```
demo/
├── src/main/java/com/ChessPTIT/
│   ├── main/
│   │   └── Main.java                    # Entry point của ứng dụng
│   ├── model/                            # Lớp mô hình (Model)
│   │   ├── Piece.java                   # Lớp trừu tượng cho quân cờ
│   │   ├── King.java, Queen.java, ...   # Các lớp quân cờ cụ thể
│   │   ├── Board.java                   # Bàn cờ
│   │   ├── Player.java                  # Người chơi
│   │   ├── Match.java                   # Thông tin trận đấu
│   │   ├── Position.java                # Tọa độ ô cờ
│   │   └── PieceColor.java              # Enum màu quân
│   ├── service/                          # Logic nghiệp vụ (Controller)
│   │   └── GameService.java             # Xử lý luật chơi và trạng thái
│   ├── view/                             # Giao diện (View)
│   │   ├── MainFrame.java               # Cửa sổ chính
│   │   ├── MainMenuPanel.java           # Menu chính
│   │   ├── GamePanel.java               # Màn hình chơi cờ
│   │   ├── HistoryPanel.java            # Màn hình lịch sử
│   │   └── PanelSwitcher.java           # Interface chuyển panel
│   └── db/                               # Tầng dữ liệu (DAO)
│       ├── DatabaseConnector.java       # Kết nối database
│       ├── DatabaseInitializer.java     # Khởi tạo schema
│       ├── MatchDAO.java                # CRUD cho matches
│       └── MoveDAO.java                 # CRUD cho moves
└── pom.xml                               # Maven configuration
```

### Các nguyên lý OOP được áp dụng

#### 1. Kế thừa (Inheritance)
```java
abstract class Piece {
    protected final PieceColor color;
    public abstract List<Position> getValidMoves(Board board, Position from, GameService service);
}

class King extends Piece { ... }
class Queen extends Piece { ... }
class Pawn extends Piece { ... }
```

#### 2. Đa hình (Polymorphism)
Mỗi loại quân cờ override phương thức `getValidMoves()` với logic riêng:
```java
// Tốt di chuyển theo chiều dọc
public List<Position> getValidMoves(...) { /* Pawn logic */ }

// Xe di chuyển theo hàng và cột
public List<Position> getValidMoves(...) { /* Rook logic */ }
```

#### 3. Đóng gói (Encapsulation)
```java
public class Board {
    private final Piece[][] squares = new Piece[8][8];  // Private data
    
    public Piece getPieceAt(Position pos) { ... }       // Public methods
    public void setPieceAt(Position pos, Piece piece) { ... }
}
```

#### 4. Trừu tượng (Abstraction)
- `Piece` là lớp trừu tượng định nghĩa hành vi chung
- `PanelSwitcher` là interface cho chức năng chuyển panel
- DAO pattern tách biệt logic truy cập dữ liệu

### Cơ sở dữ liệu

#### Schema

**Bảng `matches`**
```sql
CREATE TABLE matches (
    match_id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_white_name TEXT NOT NULL,
    player_black_name TEXT NOT NULL,
    result TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

**Bảng `moves`**
```sql
CREATE TABLE moves (
    move_id INTEGER PRIMARY KEY AUTOINCREMENT,
    match_id INTEGER NOT NULL,
    move_number INTEGER NOT NULL,
    notation TEXT NOT NULL,
    FOREIGN KEY (match_id) REFERENCES matches(match_id)
);
```

## 📚 Tài liệu tham khảo

### Tài liệu dự án

- [Workflow Document](https://docs.google.com/document/d/1kfNacdkCITwAx0CiWNdXdxGmugH1mLSvng5U2gUvjWQ/edit?usp=sharing)
- Xem thêm các file hướng dẫn trong thư mục gốc:
  - `BAO_CAO_PHAN_TICH_DU_AN_CHESSPTIT.txt`
  - `HUONG_DAN_DEMO_BAO_CAO.txt`
  - `QUICK_REFERENCE_DEMO.txt`
  - `SCREENSHOT_GUIDE.txt`

### Luật cờ vua

- [FIDE Chess Rules](https://www.fide.com/FIDE/handbook/LawsOfChess.pdf)
- [Chess Programming Wiki](https://www.chessprogramming.org/)

### Công nghệ

- [Java Swing Documentation](https://docs.oracle.com/javase/tutorial/uiswing/)
- [SQLite Documentation](https://www.sqlite.org/docs.html)
- [Maven Documentation](https://maven.apache.org/guides/)

## 👥 Đội ngũ phát triển

Dự án được phát triển bởi sinh viên PTIT trong môn Lập trình Hướng đối tượng.

## 📄 License

Dự án này được phát triển cho mục đích học tập tại Học viện Công nghệ Bưu chính Viễn thông (PTIT).

## 🤝 Đóng góp

Mọi đóng góp đều được chào đón! Vui lòng:

1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

## 📞 Liên hệ

- Repository: [https://github.com/hicastaw/Assignment_OOP](https://github.com/hicastaw/Assignment_OOP)
- Issues: [https://github.com/hicastaw/Assignment_OOP/issues](https://github.com/hicastaw/Assignment_OOP/issues)

---

**Chúc bạn chơi cờ vui vẻ! ♟️**
