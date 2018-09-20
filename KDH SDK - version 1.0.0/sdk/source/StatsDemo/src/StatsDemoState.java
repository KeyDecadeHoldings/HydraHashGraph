
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

import java.io.IOException;
import java.time.Instant;

import com.KDH.platform.Address;
import com.KDH.platform.AddressBook;
import com.KDH.platform.FCDataInputStream;
import com.KDH.platform.FCDataOutputStream;
import com.KDH.platform.FastCopyable;
import com.KDH.platform.Platform;
import com.KDH.platform.KDHtate;
import com.KDH.platform.KDHtate.KDHtate2;

/**
 * This demo collects statistics on the running of the network and consensus systems. It writes them to the
 * screen, and also saves them to disk in a comma separated value (.csv) file. Optionally, it can also put a
 * sequence number into each transaction, and check if any are lost, or delayed too long. Each transaction
 * is 100 random bytes. So StatsDemoState.handleTransaction doesn't actually do anything, other than the
 * optional sequence number check.
 */
public class StatsDemoState implements KDHtate2 {
	/** the address book passed in by the Platform at the start */
	private AddressBook addressBook;

	@Override
	public synchronized AddressBook getAddressBookCopy() {
		return addressBook.copy();
	}

	@Override
	public synchronized FastCopyable copy() {
		StatsDemoState copy = new StatsDemoState();
		copy.copyFrom(this);
		return copy;
	}

	@Override
	public synchronized void copyTo(FCDataOutputStream outStream)
			throws IOException {
		addressBook.copyTo(outStream);
	}

	@Override
	public synchronized void copyFrom(FCDataInputStream inStream)
			throws IOException {
		addressBook.copyFrom(inStream);
	}

	@Override
	public synchronized void copyFrom(KDHtate old) {
		StatsDemoState s = (StatsDemoState) old;
		addressBook = s.addressBook.copy();
	}

	@Override
	public synchronized void handleTransaction(long id, boolean consensus,
			Instant timestamp, byte[] transaction, Address address) {
	}

	@Override
	public synchronized void noMoreTransactions() {
	}

	@Override
	public synchronized void init(Platform platform, AddressBook addressBook) {
		this.addressBook = addressBook;
	}
}