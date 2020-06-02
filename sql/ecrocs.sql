use ecrocs;
DROP TABLE IF EXISTS shoes;
DROP TABLE IF EXISTS shoe_colors;
DROP TABLE IF EXISTS shoe_sizes;
DROP TABLE IF EXISTS shoe_details;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS transaction_details;

CREATE TABLE shoes (
	type VARCHAR(10) NOT NULL,
	id VARCHAR(10),
	name VARCHAR(40) NOT NULL,
	desc1 VARCHAR(40),
	desc2 VARCHAR(500),
	price NUMERIC(4,2),
    PRIMARY KEY(id)
);
CREATE TABLE shoe_colors(
	shoe_id VARCHAR(10) REFERENCES shoes.id,
    color_name VARCHAR(50),
    color_hex VARCHAR(7),
    file_name VARCHAR(20),
	PRIMARY KEY(shoe_id, color_name, color_hex)
);
CREATE TABLE shoe_sizes(
	shoe_id VARCHAR(10) references shoes.id,
    size INT,
    primary key(shoe_id, size)
);
CREATE TABLE shoe_details(
	id INT,
	shoe_id VARCHAR(10) references shoes.id,
    details VARCHAR(100),
    PRIMARY KEY (id, shoe_id, details)
);

CREATE TABLE transactions (
	id INT auto_increment,

    billing_full_name VARCHAR(128),
	billing_phone_number VARCHAR(12),
    billing_email VARCHAR(128),
    billing_addr_1 VARCHAR(128),
    billing_city VARCHAR(128),
    billing_state VARCHAR(128),
    billing_zip VARCHAR(10),
    
    shipping_method VARCHAR(50),
    
    payment_name VARCHAR(128),
    payment_card VARCHAR(16),
    payment_exp_month VARCHAR(2),
    payment_exp_year VARCHAR(4),
    
    PRIMARY KEY (id)
);

CREATE TABLE transaction_details (
	transaction_id INT references transactions.id,
	shoe_id VARCHAR(10) references shoes.id,
    color_name VARCHAR(50) references shoe_colors.color_name,
    quantity INT,
    shoe_size INT,
    
	base_price DOUBLE(4,2), 
    state_tax DOUBLE(4,2),
    
    PRIMARY KEY(transaction_id, shoe_id, color_name, quantity, shoe_size)
);