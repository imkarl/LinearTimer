# LinearTimer
对齐计时管理器（整秒对齐）

### 开启计时器
    LinearTimer.start();

### 停止计时器
    LinearTimer.stop();

### 添加计时监听
    LinearTimer.addTimeListener(System.currentTimeMillis()+2*60*1000, listener);

### 添加计时监听
    LinearTimer.addCountListener(10, listener);
