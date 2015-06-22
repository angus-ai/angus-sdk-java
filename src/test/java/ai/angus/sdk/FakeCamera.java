package ai.angus.sdk;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

public class FakeCamera implements Iterator<File> {
    private Iterator<File> videos = null;

    public FakeCamera(String path) {
        File dir = new File(path);
        File[] tmp = dir.listFiles();
        Arrays.sort(tmp);
        videos = Arrays.asList(tmp).iterator();
    }

    @Override
    public boolean hasNext() {
        return videos.hasNext();
    }

    @Override
    public File next() {
        return videos.next();
    }

    @Override
    public void remove() {
        throw new RuntimeException("Immutable iterator");
    }
}
