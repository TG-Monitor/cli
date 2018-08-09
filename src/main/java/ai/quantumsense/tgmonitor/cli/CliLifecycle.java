package ai.quantumsense.tgmonitor.cli;

/**
 * Interface through which the CLI instance signalises its creation and
 * destruction to interested parties (for example, an IPC component).
 */
public interface CliLifecycle {
    /**
     * Called by a CLI instance immediately after it has been created.
     */
    void start();

    /**
     * Called by a CLI instance immediately before it terminates.
     */
    void stop();
}
