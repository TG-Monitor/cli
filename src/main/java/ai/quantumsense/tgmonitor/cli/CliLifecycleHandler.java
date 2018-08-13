package ai.quantumsense.tgmonitor.cli;

/**
 * Interface through which the CLI instance signalises its creation and
 * destruction to interested parties (for example, an IPC component).
 */
public interface CliLifecycleHandler {
    /**
     * Called when CLI instance is created.
     */
    void onCliCreate();

    /**
     * Called when CLI instance is destroyed.
     */
    void onCliDestroy();
}
