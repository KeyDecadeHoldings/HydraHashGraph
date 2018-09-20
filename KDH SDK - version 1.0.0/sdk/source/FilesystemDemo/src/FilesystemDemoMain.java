
/*
 * This file is public domain.
 *
 * KDH MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF 
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. KDH SHALL NOT BE LIABLE FOR 
 * ANY DAMAGES SUFFERED AS A RESULT OF USING, MODIFYING OR 
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import java.util.*;

import com.KDH.platform.*;

/**
 * A simple text editor application that saves its files to a KDH Fast Copyable Filesystem. Every save
 * is a transaction propagated across the network to peers. Thus, the filesystem is kept synchronized across
 * all nodes.
 */
public class FilesystemDemoMain implements KDHMain {
	/** the platform running this app */
	public Platform platform;
	/** the ID number for this member */
	private long selId;

	/**
	 * This is just for debugging: it allows the app to run in Eclipse. If the config.txt exists and lists a
	 * particular KDHMain class as the one to run, then it can run in Eclipse (with the green triangle
	 * icon).
	 *
	 * @param args
	 *            these are not used
	 */
	public static void main(String[] args) {
		Browser.main(args);
	}

	@Override
	public void preEvent() {
	}

	@Override
	public void init(Platform platform, long id) {
		this.platform = platform;
		this.selId = id;
		platform.setAbout("Filesystem Demo v. 1.0\n"); // set the browser's "about" box
		platform.setSleepAfterSync(250); // milliseconds
	}

	/**
	 * Start the text editor GUI. Then update its status bar every time the underlying filesystem is found
	 * to have changed, indicating that files have arrived from the network (or local node).
	 */
	@Override
	public void run() {
		try {
			TextEditor wp = TextEditor.openOn(platform);
			byte[] fsHash = fsHash();
			while (true) {
				Thread.sleep(1000);
				byte[] newHash = fsHash();
				if (!Arrays.equals(fsHash, newHash))
					wp.status(String.format("filesystem changed at %s",
							new Date()));
				fsHash = newHash;
			}
		} catch (InterruptedException e) {
			System.err.println(String.format("[FilesystemDemo %d] interrupted",
					selId));
		}
	}

	/** @return The hash of (the root directory of) the fast copyable filesystem */
	private byte[] fsHash() {
		return ((FilesystemDemoState) platform.getState()).getFS().fcNamei("/");
	}

	@Override
	public KDHtate newState() {
		return new FilesystemDemoState();
	}
}
