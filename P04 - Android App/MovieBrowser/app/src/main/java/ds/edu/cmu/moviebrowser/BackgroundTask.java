/**
 * @author  Kavi Sarna
 * @email   ksarna@andrew.cmu.edu
 */
package ds.edu.cmu.moviebrowser;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BackgroundTask<T> {
    private final Supplier<T> supplier;
    private final Consumer<T> consumer;
    private final ExecutorService executor;
    private final Handler handler;

    public BackgroundTask(Supplier<T> supplier, Consumer<T> consumer) {
        this.supplier = supplier;
        this.consumer = consumer;
        this.executor = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void execute() {
        executor.execute(() -> {
            T result = supplier.get();
            handler.post(() -> {
                consumer.accept(result);
                executor.shutdown();
            });
        });
    }
}
