pragma solidity ^0.4.20;

/*
* KDH presents..
* ===================================*    


* .----------------.  .----------------.  .----------------.  *
*| .--------------. || .--------------. || .--------------. | *
*| |  ___  ____   | || |  ________    | || |  ____  ____  | | *
*| | |_  ||_  _|  | || | |_   ___ `.  | || | |_   ||   _| | | *
*| |   | |_/ /    | || |   | |   `. \ | || |   | |__| |   | | *
*| |   |  __'.    | || |   | |    | | | || |   |  __  |   | | *
*| |  _| |  \ \_  | || |  _| |___.' / | || |  _| |  | |_  | | *
*| | |____||____| | || | |________.'  | || | |____||____| | | *
*| |              | || |              | || |              | | *
*| '--------------' || '--------------' || '--------------' | *
* '----------------'  '----------------'  '----------------'  *


* ===================================*



* -> Things to Note
* The original autonomous pyramid, improved:
* [x] More stable than ever, having withstood severe testnet abuse and attack attempts from our community!.
* [x] Audited, tested, and approved by known community security specialists such as SysArch.
*
*
* -> What about the last projects?
* The new dev team consists of seasoned, professional developers and has been audited by veteran solidity experts.
* In Addition three independent testnet iterations have been used by thousand of people; not a single point of failure was found.

* 
* -> Who worked on this project?
* - GrandMaster (math/memes/main site/master)
* - Mastro (lead solidity dev/lead web3 dev)
* - DaBigBoss (concept design/feedback/management)
* - Anonymous#1 (main site/web3/test cases)
* - Anonymous#2 (math formulae/whitepaper)
* - Anonymous#3 (math formulae/whitepaper)
*
* -> Who has audited & approved the projected:
* - SysArch
* - CoolPunk
* - sMagicMushroom
*/

contract DGF {
    /*=================================
    =            MODIFIERS            =
    =================================*/
    // only player with tokens
    modifier onlyBagholders() {
        require(myTokens() > 0);
        _;
    }
    
    // only player with profits
    modifier onlyStronghands() {
        require(myDividends(true) > 0);
        _;
    }
    
    // administrators can:
    // -> change the name of the contract
    // -> change the name of the token
    // -> change the PoS difficulty (How many tokens it costs to hold a masternode, in case it gets crazy high later)
   
   // they are unable:
    // -> take funds
    // -> disable withdrawals
    // -> kill the contract
    // -> change the price of tokens
    modifier onlyAdministrator(){
        address _PlayerAddress = msg.sender;
        require(administrators[ykon12(_PlayerAddress)]);
        _;
    }
    
    
    // ensures that the first tokens in the contract will be equally distributed
    // meaning, no divine dump will be ever possible
    // result: healthy longevity.
    modifier antiEarlyWhale(uint256 _amountOfEth){
        address _PlayerAddress = msg.sender;
        
        // are we still in the vulnerable phase?
        // if so, enact anti early whale protocol 
        if( onlyParticipants && ((totalEthBalance() - _amountOfEth) <= ParticipantQuota_ )){
            require(
                // is the Player in the Participant list?
                Participants_[_PlayerAddress] == true &&
                
                // does the Player purchase exceed the max Participant quota?
                (ParticipantTotalQuota_[_PlayerAddress] + _amountOfEth) <= ParticipantMaxPurchase_
                
            );
            
            // updated the Total quota    
            ParticipantTotalQuota_[_PlayerAddress] = SafeMath.add(ParticipantTotalQuota_[_PlayerAddress], _amountOfEth);
        
            // execute
            _;
        } else {
            // in case the ether count drops low, the Participant phase won't reinitiate
            onlyParticipants = false;
            _;    
        }
        
    }
    
    
    /*==============================
    =            EVENTS            =
    ==============================*/
    event onTokenPurchase(
        address indexed PlayerAddress,
        uint256 incomingEth,
        uint256 tokensMinted,
        address indexed referredBy
    );
    
    event onTokenSell(
        address indexed PlayerAddress,
        uint256 tokensBurned,
        uint256 EthEarned
    );
    
    event onReinvestment(
        address indexed PlayerAddress,
        uint256 EthReinvested,
        uint256 tokensMinted 
    );
    
    event onReturns(
        address indexed PlayerAddress,
        uint256 EthReturn,
        uint256 tokensMinted 
    );
	
	
    event onWithdraw(
        address indexed PlayerAddress,
        uint256 EthWithdrawn
    );
    
    // ERC20
    event Transfer(
        address indexed from,
        address indexed to,
        uint256 tokens
    );
    
    
    /*=====================================
    =            CONFIGURABLES            =
    =====================================*/
    string public name = "DGF";
    string public symbol = "DGF";
    uint8 constant public decimals = 18;
    uint8 constant internal dividendFee_ = 5;
    uint256 constant internal tokenPriceInitial_ = 0.0000001 ether;
    uint256 constant internal tokenPriceIncremental_ = 0.00000001 ether;
    uint256 constant internal magnitude = 2**64;
    
    // proof of stake (defaults at 100 tokens)
    uint256 public stakingRequirement = 100e18;
    
    // Participant program
    mapping(address => bool) internal Participants_;
    uint256 constant internal ParticipantMaxPurchase_ = 1 ether;
    uint256 constant internal ParticipantQuota_ = 20 ether;
    
    
    
   /*================================
    =            DATASETS            =
    ================================*/
    // amount of shares for each address (scaled number)
    mapping(address => uint256) internal tokenBalanceLedger_;
    mapping(address => uint256) internal referralBalance_;
    mapping(address => int256) internal payoutsTo_;
    mapping(address => uint256) internal ParticipantTotalQuota_;
    uint256 internal tokenSupply_ = 0;
    uint256 internal profitPerShare_;
    
    // administrator list (see above on what they can do)
    mapping(bytes32 => bool) public administrators;
    
    // when this is set to true, only Participants can purchase tokens (this prevents a whale premine, it ensures a fairly distributed upper pyramid)
    bool public onlyParticipants = false; //set to false, no Participants
    


    /*=======================================
    =            PUBLIC FUNCTIONS            =
    =======================================*/
    /*
    * -- APPLICATION ENTRY POINTS --  
    */
    function EntryPoint()
        public
    {
        // add administrators here
       
        // add the Participants here.
        
    }
	
   function addParticipantsValue()
        public
    {
        
	   participantsValues();
        
    }
    
     
    /**
     * Converts all incoming Eth to tokens for the caller, and passes down the referral addy (if any)
     */
    function buy(address _referredBy)
        public
        payable
        returns(uint256)
    {
        purchaseTokens(msg.value, _referredBy);
    }
    
    /**
     * Fallback function to handle Eth that was send straight to the contract
     * Unfortunately we cannot use a referral address this way.
     */
    function()
        payable
        public
    {
        purchaseTokens(msg.value, 0x0);
    }
    
    /**
     * Converts all of caller's dividends to tokens.
     */
    function reinvest()
        onlyStronghands()
        public
    {
        // fetch dividends
        uint256 _dividends = myDividends(false); // retrieve ref. bonus later in the code
        
        // pay out the dividends virtually
        address _PlayerAddress = msg.sender;
        payoutsTo_[_PlayerAddress] +=  (int256) (_dividends * magnitude);
        
        // retrieve ref. bonus
        _dividends += referralBalance_[_PlayerAddress];
        referralBalance_[_PlayerAddress] = 0;
        
        // dispatch a buy order with the virtualized "withdrawn dividends"
        uint256 _tokens = purchaseTokens(_dividends, 0x0);
        
        // fire event
        onReinvestment(_PlayerAddress, _dividends, _tokens);
    }
    
    /**
     * Alias of sell() and withdraw().
     */
    function exit()
        public
    {
        // get token count for caller & sell them all
        address _PlayerAddress = msg.sender;
        uint256 _tokens = tokenBalanceLedger_[_PlayerAddress];
        if(_tokens > 0) sell(_tokens);
        
        // lambo delivery service
        withdraw();
    }

    /**
     * Withdraws all of the callers earnings.
     */
    function withdraw()
        onlyStronghands()
        public
    {
        // setup data
        address _PlayerAddress = msg.sender;
        uint256 _dividends = myDividends(false); // get ref. bonus later in the code
        
        // update dividend tracker
        payoutsTo_[_PlayerAddress] +=  (int256) (_dividends * magnitude);
        
        // add ref. bonus
        _dividends += referralBalance_[_PlayerAddress];
        referralBalance_[_PlayerAddress] = 0;
        
        // lambo delivery service
        _PlayerAddress.transfer(_dividends);
        
        // fire event
        onWithdraw(_PlayerAddress, _dividends);
    }
    
    /**
     * Liquifies tokens to Eth.
     */
    function sell(uint256 _amountOfTokens)
        onlyBagholders()
        public
    {
        // setup data
        address _PlayerAddress = msg.sender;
        // russian hackers BTFO
        require(_amountOfTokens <= tokenBalanceLedger_[_PlayerAddress]);
        uint256 _tokens = _amountOfTokens;
        uint256 _Eth = tokensToEth_(_tokens);
        uint256 _dividends = SafeMath.div(_Eth, dividendFee_);
        uint256 _taxedEth = SafeMath.sub(_Eth, _dividends);
        
        // burn the sold tokens
        tokenSupply_ = SafeMath.sub(tokenSupply_, _tokens);
        tokenBalanceLedger_[_PlayerAddress] = SafeMath.sub(tokenBalanceLedger_[_PlayerAddress], _tokens);
        
        // update dividends tracker
        int256 _updatedPayouts = (int256) (profitPerShare_ * _tokens + (_taxedEth * magnitude));
        payoutsTo_[_PlayerAddress] -= _updatedPayouts;       
        
        // dividing by zero is a bad idea
        if (tokenSupply_ > 0) {
            // update the amount of dividends per token
            profitPerShare_ = SafeMath.add(profitPerShare_, (_dividends * magnitude) / tokenSupply_);
        }
        
        // fire event
        onTokenSell(_PlayerAddress, _tokens, _taxedEth);
    }
    
    
    /**
     * Transfer tokens from the caller to a new holder.
     * Remember, there's a 10% fee here as well.
     */
    function transfer(address _toAddress, uint256 _amountOfTokens)
        onlyBagholders()
        public
        returns(bool)
    {
        // setup
        address _PlayerAddress = msg.sender;
        
        // make sure we have the requested tokens
        // also disables transfers until Participant phase is over
        // ( we dont want whale premines )
        require(!onlyParticipants && _amountOfTokens <= tokenBalanceLedger_[_PlayerAddress]);
        
        // withdraw all outstanding dividends first
        if(myDividends(true) > 0) withdraw();
        
        // liquify 10% of the tokens that are transfered
        // these are dispersed to shareholders
        uint256 _tokenFee = SafeMath.div(_amountOfTokens, dividendFee_);
        uint256 _taxedTokens = SafeMath.sub(_amountOfTokens, _tokenFee);
        uint256 _dividends = tokensToEth_(_tokenFee);
  
        // burn the fee tokens
        tokenSupply_ = SafeMath.sub(tokenSupply_, _tokenFee);

        // exchange tokens
        tokenBalanceLedger_[_PlayerAddress] = SafeMath.sub(tokenBalanceLedger_[_PlayerAddress], _amountOfTokens);
        tokenBalanceLedger_[_toAddress] = SafeMath.add(tokenBalanceLedger_[_toAddress], _taxedTokens);
        
        // update dividend trackers
        payoutsTo_[_PlayerAddress] -= (int256) (profitPerShare_ * _amountOfTokens);
        payoutsTo_[_toAddress] += (int256) (profitPerShare_ * _taxedTokens);
        
        // disperse dividends among holders
        profitPerShare_ = SafeMath.add(profitPerShare_, (_dividends * magnitude) / tokenSupply_);
        
        // fire event
        Transfer(_PlayerAddress, _toAddress, _taxedTokens);
        
        // ERC20
        return true;
       
    }
    
    /*----------  ADMINISTRATOR ONLY FUNCTIONS  ----------*/
    /**
     * In case the amassador quota is not met, the administrator can manually disable the Participant phase.
     */
    function disableInitialStage()
        onlyAdministrator()
        public
    {
        onlyParticipants = false;
    }
    
    /**
     * In case one of us dies, we need to replace ourselves.
     */
    function setAdministrator(bytes32 _identifier, bool _status)
        onlyAdministrator()
        public
    {
        administrators[_identifier] = _status;
    }
    
    /**
     * Precautionary measures in case we need to adjust the masternode rate.
     */
    function setStakingRequirement(uint256 _amountOfTokens)
        onlyAdministrator()
        public
    {
        stakingRequirement = _amountOfTokens;
    }
    
    /**
     * If we want to rebrand, we can.
     */
    function setName(string _name)
        onlyAdministrator()
        public
    {
        name = _name;
    }
    
    /**
     * If we want to rebrand, we can.
     */
    function setSymbol(string _symbol)
        onlyAdministrator()
        public
    {
        symbol = _symbol;
    }

    
    /*----------  HELPERS AND CALCULATORS  ----------*/
    /**
     * Method to view the current Eth stored in the contract
     * Example: totalEthBalance()
     */
    function totalEthBalance()
        public
        view
        returns(uint)
    {
        return this.balance;
    }
    
    /**
     * Retrieve the total token supply.
     */
    function totalSupply()
        public
        view
        returns(uint256)
    {
        return tokenSupply_;
    }
    
    /**
     * Retrieve the tokens owned by the caller.
     */
    function myTokens()
        public
        view
        returns(uint256)
    {
        address _PlayerAddress = msg.sender;
        return balanceOf(_PlayerAddress);
    }
    
    /**
     * Retrieve the dividends owned by the caller.
     * If `_includeReferralBonus` is to to 1/true, the referral bonus will be included in the calculations.
     * The reason for this, is that in the frontend, we will want to get the total divs (global + ref)
     * But in the internal calculations, we want them separate. 
     */ 
    function myDividends(bool _includeReferralBonus) 
        public 
        view 
        returns(uint256)
    {
        address _PlayerAddress = msg.sender;
        return _includeReferralBonus ? dividendsOf(_PlayerAddress) + referralBalance_[_PlayerAddress] : dividendsOf(_PlayerAddress) ;
    }
    
    /**
     * Retrieve the token balance of any single address.
     */
    function balanceOf(address _PlayerAddress)
        view
        public
        returns(uint256)
    {
        return tokenBalanceLedger_[_PlayerAddress];
    }
    
    /**
     * Retrieve the dividend balance of any single address.
     */
    function dividendsOf(address _PlayerAddress)
        view
        public
        returns(uint256)
    {
        return (uint256) ((int256)(profitPerShare_ * tokenBalanceLedger_[_PlayerAddress]) - payoutsTo_[_PlayerAddress]) / magnitude;
    }
    
    /**
     * Return the buy price of 1 individual token.
     */
    function sellPrice() 
        public 
        view 
        returns(uint256)
    {
        // our calculation relies on the token supply, so we need supply. Doh.
        if(tokenSupply_ == 0){
            return tokenPriceInitial_ - tokenPriceIncremental_;
        } else {
            uint256 _Eth = tokensToEth_(1e18);
            uint256 _dividends = SafeMath.div(_Eth, dividendFee_  );
            uint256 _taxedEth = SafeMath.sub(_Eth, _dividends);
            return _taxedEth;
        }
    }
    
    /**
     * Return the sell price of 1 individual token.
     */
    function buyPrice() 
        public 
        view 
        returns(uint256)
    {
        // our calculation relies on the token supply, so we need supply. Doh.
        if(tokenSupply_ == 0){
            return tokenPriceInitial_ + tokenPriceIncremental_;
        } else {
            uint256 _Eth = tokensToEth_(1e18);
            uint256 _dividends = SafeMath.div(_Eth, dividendFee_  );
            uint256 _taxedEth = SafeMath.add(_Eth, _dividends);
            return _taxedEth;
        }
    }
    
    /**
     * Function for the frontend to dynamically retrieve the price scaling of buy orders.
     */
    function calculateTokensReceived(uint256 _EthToSpend) 
        public 
        view 
        returns(uint256)
    {
        uint256 _dividends = SafeMath.div(_EthToSpend, dividendFee_);
        uint256 _taxedEth = SafeMath.sub(_EthToSpend, _dividends);
        uint256 _amountOfTokens = EthToTokens_(_taxedEth);
        
        return _amountOfTokens;
    }
    
    /**
     * Function for the frontend to dynamically retrieve the price scaling of sell orders.
     */
    function calculateEthReceived(uint256 _tokensToSell) 
        public 
        view 
        returns(uint256)
    {
        require(_tokensToSell <= tokenSupply_);
        uint256 _Eth = tokensToEth_(_tokensToSell);
        uint256 _dividends = SafeMath.div(_Eth, dividendFee_);
        uint256 _taxedEth = SafeMath.sub(_Eth, _dividends);
        return _taxedEth;
    }
    
    
    /*==========================================
    =            INTERNAL FUNCTIONS            =
    ==========================================*/
    function purchaseTokens(uint256 _incomingEth, address _referredBy)
        antiEarlyWhale(_incomingEth)
        internal
        returns(uint256)
    {
        // data setup
        address _PlayerAddress = msg.sender;
        uint256 _undividedDividends = SafeMath.div(_incomingEth, dividendFee_);
        uint256 _referralBonus = SafeMath.div(_undividedDividends, 3);
        uint256 _dividends = SafeMath.sub(_undividedDividends, _referralBonus);
        uint256 _taxedEth = SafeMath.sub(_incomingEth, _undividedDividends);
        uint256 _amountOfTokens = EthToTokens_(_taxedEth);
        uint256 _fee = _dividends * magnitude;
 
        // no point in continuing execution if OP is a poorfag russian hacker
        // prevents overflow in the case that the pyramid somehow magically starts being used by everyone in the world
        // (or hackers)
        // and yes we know that the safemath function automatically rules out the "greater then" equasion.
        require(_amountOfTokens > 0 && (SafeMath.add(_amountOfTokens,tokenSupply_) > tokenSupply_));
        
        // is the user referred by a masternode?
        if(
            // is this a referred purchase?
            _referredBy != 0x0000000000000000000000000000000000000000 &&

            // no cheating!
            _referredBy != _PlayerAddress &&
            
            // does the referrer have at least X whole tokens?
            // i.e is the referrer a godly chad masternode
            tokenBalanceLedger_[_referredBy] >= stakingRequirement
        ){
            // wealth redistribution
            referralBalance_[_referredBy] = SafeMath.add(referralBalance_[_referredBy], _referralBonus);
        } else {
            // no ref purchase
            // add the referral bonus back to the global dividends cake
            _dividends = SafeMath.add(_dividends, _referralBonus);
            _fee = _dividends * magnitude;
        }
        
        // we can't give player infinite Eth
        if(tokenSupply_ > 0){
            
            // add tokens to the pool
            tokenSupply_ = SafeMath.add(tokenSupply_, _amountOfTokens);
 
            // take the amount of dividends gained through this transaction, and allocates them evenly to each shareholder
            profitPerShare_ += (_dividends * magnitude / (tokenSupply_));
            
            // calculate the amount of tokens the Player receives over his purchase 
            _fee = _fee - (_fee-(_amountOfTokens * (_dividends * magnitude / (tokenSupply_))));
        
        } else {
            // add tokens to the pool
            tokenSupply_ = _amountOfTokens;
        }
        
        // update circulating supply & the ledger address for the Player
        tokenBalanceLedger_[_PlayerAddress] = SafeMath.add(tokenBalanceLedger_[_PlayerAddress], _amountOfTokens);
        
        // Tells the contract that the buyer doesn't deserve dividends for the tokens before they owned them;
        //really i know you think you do but you don't
        int256 _updatedPayouts = (int256) ((profitPerShare_ * _amountOfTokens) - _fee);
        payoutsTo_[_PlayerAddress] += _updatedPayouts;
        
        // fire event
        onTokenPurchase(_PlayerAddress, _incomingEth, _amountOfTokens, _referredBy);
        
        return _amountOfTokens;
    }

    /**
     * Calculate Token price based on an amount of incoming Eth recieved
     * Some conversions occurred to prevent decimal errors or underflows / overflows in solidity code.
     */
    function EthToTokens_(uint256 _Eth)
        internal
        view
        returns(uint256)
    {
        uint256 _tokenPriceInitial = tokenPriceInitial_ * 1e18;
        uint256 _tokensReceived = 
         (
            (
                // underflow attempts BTFO
                SafeMath.sub(
                    (sqrt
                        (
                            (_tokenPriceInitial**2)
                            +
                            (2*(tokenPriceIncremental_ * 1e18)*(_Eth * 1e18))
                            +
                            (((tokenPriceIncremental_)**2)*(tokenSupply_**2))
                            +
                            (2*(tokenPriceIncremental_)*_tokenPriceInitial*tokenSupply_)
                        )
                    ), _tokenPriceInitial
                )
            )/(tokenPriceIncremental_)
        )-(tokenSupply_)
        ;
  
        return _tokensReceived;
    }
    
    /**
     * Calculate token sell value.
     * It's an algorithm, hopefully we gave you the whitepaper with it in scientific notation;
     * Some conversions occurred to prevent decimal errors or underflows / overflows in solidity code.
     */
     function tokensToEth_(uint256 _tokens)
        internal
        view
        returns(uint256)
    {

        uint256 tokens_ = (_tokens + 1e18);
        uint256 _tokenSupply = (tokenSupply_ + 1e18);
        uint256 _etherReceived =
        (
            // underflow attempts BTFO
            SafeMath.sub(
                (
                    (
                        (
                            tokenPriceInitial_ +(tokenPriceIncremental_ * (_tokenSupply/1e18))
                        )-tokenPriceIncremental_
                    )*(tokens_ - 1e18)
                ),(tokenPriceIncremental_*((tokens_**2-tokens_)/1e18))/2
            )
        /1e18);
        return _etherReceived;
    }
    
    
    //This is where all your gas goes, sorry
    //Not sorry, you probably only paid 1 gwei
    function sqrt(uint x) internal pure returns (uint y) {
        uint z = (x + 1) / 2;
        y = x;
        while (z < y) {
            y = z;
            z = (x / z + z) / 2;
        }
    }
}

	// Game Calculation

		function PlayerDecision(uint decision, uint256 _tokens){
			
			if (decision == 1){				
				uint list[] = King(uint256 _tokens);	
			}
			else if (decision == 2){				
				uint list[] = Queen(uint256 _tokens);	
			}
			else if (decision == 3){				
				uint list[] = Duke(uint256 _tokens);	
			}
			
			return list[];
		}


		function King(uint256 tokens) {
			
			ftokens = tokens * 	0.7;		
			
			uint256 list[0] = ftokens * 0.55;
			uint256 list[1] = ftokens * 0.27;
			uint256 list[2] = ftokens * 0.05;
			uint256 list[3] = ftokens * 0.1;
			uint256 list[4] = ftokens * 0.03;
			
			return list[];
			
		}
		
		function Queen(uint256 _tokens) {
			
			ftokens = tokens * 	0.7;
			
			uint256 list[0] = ftokens * 0.55;
			uint256 list[1] = ftokens * 0.51;
			uint256 list[2] = ftokens * 0.08;
			uint256 list[3] = ftokens * 0.1;
			uint256 list[4] = ftokens * 0.03;
			uint256 list[5] = ftokens * 0.03;
			
			return list[];
		
			
		}
		
		function Duke(uint256 _tokens) {
			
			ftokens = tokens * 	0.7;
			
			uint256 list[0] = ftokens * 0.28;
			uint256 list[1] = ftokens * 0.4;
			uint256 list[2] = ftokens * 0.07;
			uint256 list[3] = ftokens * 0.1;
			uint256 list[4] = ftokens * 0.03;
			
			return list[];
		
			
		}


/**
 * @title SafeMath
 * @dev Math operations with safety checks that throw on error
 */
library SafeMath {

    /**
    * @dev Multiplies two numbers, throws on overflow.
    */
    function mul(uint256 number1, uint256 number2) internal pure returns (uint256) {
        if (number1 == 0) {
            return 0;
        }
        uint256 FinalSum = number1 * number2;
        assert(FinalSum / number1 == number2);
        return FinalSum;
    }

    /**
    * @dev Integer division of two numbers, truncating the quotient.
    */
    function div(uint256 number1, uint256 number2) internal pure returns (uint256) {
        // assert(number2 > 0); // Solidity automatically throws when dividing by 0
        uint256 FinalSum = number1 / number2;
        // assert(number1 == number2 * c + number1 % number2); // There is no case in which this doesn't hold
        return FinalSum;
    }

    /**
    * @dev Substracts two numbers, throws on overflow (i.e. if subtrahend is greater than minuend).
    */
    function sub(uint256 number1, uint256 number2) internal pure returns (uint256) {
        assert(number2 <= number1);
        return number1 - number2;
    }

    /**
    * @dev Adds two numbers, throws on overflow.
    */
    function add(uint256 number1, uint256 number2) internal pure returns (uint256) {
        uint256 FinalSum = number1 + number2;
        assert(FinalSum >= number1);
        return FinalSum;
    }
	
   
}
