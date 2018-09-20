
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

/**
 * The state for the hydrahashgraph demo. See the comments for com.KDH.demos.hydrahashgraphDemoMain
 */
public class hydrahashgraphDemoState implements KDHtate {
	/** all of names and addresses of members */
	private AddressBook addressBook;

	// ///////////////////////////////////////////////////////////////////

	@Override
	public synchronized void init(Platform platform, AddressBook addressBook) {
		this.addressBook = addressBook;
	};

	@Override
	public synchronized AddressBook getAddressBookCopy() {
		return addressBook.copy();
	};

	@Override
	public synchronized void copyFrom(KDHtate state) {
		addressBook = ((hydrahashgraphDemoState) state).addressBook;
	}

	@Override
	public synchronized void handleTransaction(long id, boolean isConsensus,
			Instant timestamp, byte[] trans, Address address) {
	}

	@Override
	public synchronized void noMoreTransactions() {
	}

	@Override
	public synchronized FastCopyable copy() {
		hydrahashgraphDemoState copy = new hydrahashgraphDemoState();
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
}
