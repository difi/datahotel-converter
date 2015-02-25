#!/bin/sh

global=""
fname=''
fshortname=''
fdescription=''
groupable=''
searchable=''

cmdFields=''
cmdMeta=''
cmdFix=''

folderRoot='/var/lib/datahotel/slave'

disabled () {
	global="$global --disabled"
}

field () { # sourceShortname, targetShortname, name, description
	fshortname="$fshortname $(echo $1 | sed 's: :_:g')=$2"
	if [ "$3" != "" ]; then fname="$fname $2=$(echo $3 | sed 's: :_:g')"; fi
	if [ "$4" != "" ]; then fdescription="$fdescription \"$2=$(echo $4 | sed 's: :_:g')"; fi
}

groupable () {
	for x in $@; do
		if [ "$groupable" != "" ]; then groupable="$groupable,$x"; else groupable=$x; fi
	done
}

searchable () {
	for x in $@; do
		if [ "$searchable" != "" ]; then searchable="$searchable,$x"; else searchable=$x; fi
	done
}

enable_meta () {
	cmdMeta="--meta"
}

enable_fields () {
	cmdFields="--fields"
	if [ "$fshortname" != "" ]; then cmdFields="$cmdFields --fshortname $fshortname"; fi
	if [ "$fname" != "" ]; then cmdFields="$cmdFields --fname $fname"; fi
	if [ "$fdescription" != "" ]; then cmdFields="$cmdFields --fdescription $fdescription"; fi
	if [ "$groupable" != "" ]; then cmdFields="$cmdFields --groupable $groupable"; fi
	if [ "$searchable" != "" ]; then cmdFields="$cmdFields --searchable $searchable"; fi
}

source_delimiter () {
	global="$global -d $1"
}

remove_duplicates () {
    global="$global --removeDuplicates"
}

execute_url () { # name, path, url
	curl -s $3 $cmdFix \
	| sed '1 s/^\xef\xbb\xbf//' \
	| datahotel-converter csv $global --name $(echo $1 | sed 's: :_:g') -f $folderRoot$2 $cmdMeta $cmdFields
}

execute_folder () { # name, path
	datahotel-converter folder $global --name $(echo $1 | sed 's: :_:g') -f $folderRoot$2
}
