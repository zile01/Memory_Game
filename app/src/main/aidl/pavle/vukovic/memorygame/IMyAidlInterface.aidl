// IMyAidlInterface.aidl
package pavle.vukovic.memorygame;

interface IMyAidlInterface {
    void refresh();
    boolean getFlag();
    void setFlag();
    boolean getRun();
    void setRun(boolean bool);
}